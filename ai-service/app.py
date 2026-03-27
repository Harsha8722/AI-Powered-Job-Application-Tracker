import os
import re
import json
import logging
from flask import Flask, request, jsonify
from flask_cors import CORS
import spacy
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import PyPDF2
import docx
import io
import numpy as np

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

try:
    nlp = spacy.load("en_core_web_sm")
    logger.info("spaCy model loaded successfully")
except OSError:
    logger.warning("spaCy model not found. Run: python -m spacy download en_core_web_sm")
    nlp = None

TECH_SKILLS = [
    "python", "java", "javascript", "typescript", "react", "angular", "vue", "node.js",
    "spring", "spring boot", "django", "flask", "fastapi", "express",
    "sql", "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
    "docker", "kubernetes", "aws", "azure", "gcp", "terraform",
    "git", "github", "gitlab", "jenkins", "ci/cd",
    "machine learning", "deep learning", "tensorflow", "pytorch", "scikit-learn",
    "pandas", "numpy", "data science", "nlp", "spacy",
    "rest api", "graphql", "microservices", "kafka", "rabbitmq",
    "html", "css", "sass", "bootstrap", "tailwind",
    "linux", "bash", "powershell", "agile", "scrum", "jira",
    "maven", "gradle", "npm", "webpack", "vite",
    "hibernate", "jpa", "jdbc", "junit", "mockito",
    "selenium", "pytest", "jest", "cypress",
    "c++", "c#", "go", "rust", "kotlin", "swift",
    "hadoop", "spark", "hive", "databricks",
    "jwt", "oauth", "security", "encryption",
    "excel", "tableau", "power bi",
]

def extract_text_from_pdf(file_bytes):
    try:
        reader = PyPDF2.PdfReader(io.BytesIO(file_bytes))
        text = ""
        for page in reader.pages:
            extracted = page.extract_text()
            if extracted:
                text += extracted + "\n"
        return text.strip()
    except Exception as e:
        logger.error(f"PDF extraction error: {e}")
        return ""

def extract_text_from_docx(file_bytes):
    try:
        doc = docx.Document(io.BytesIO(file_bytes))
        text = "\n".join([para.text for para in doc.paragraphs])
        return text.strip()
    except Exception as e:
        logger.error(f"DOCX extraction error: {e}")
        return ""

def extract_skills(text):
    text_lower = text.lower()
    detected_skills = []
    for skill in TECH_SKILLS:
        if skill.lower() in text_lower:
            detected_skills.append(skill)
    if nlp:
        doc = nlp(text[:5000])
        for ent in doc.ents:
            if ent.label_ in ["ORG", "PRODUCT", "WORK_OF_ART"]:
                skill_candidate = ent.text.lower()
                if len(skill_candidate) > 2 and skill_candidate not in detected_skills:
                    detected_skills.append(skill_candidate)
    return list(set(detected_skills))

def extract_email(text):
    pattern = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b'
    emails = re.findall(pattern, text)
    return emails[0] if emails else None

def extract_phone(text):
    pattern = r'[\+\(]?[1-9][0-9 .\-\(\)]{8,}[0-9]'
    phones = re.findall(pattern, text)
    return phones[0] if phones else None

def calculate_experience_years(text):
    pattern = r'(\d+)\+?\s*(?:years?|yrs?)'
    matches = re.findall(pattern, text.lower())
    if matches:
        return max(int(m) for m in matches)
    return 0

def has_section(text_lower, keywords):
    return any(kw in text_lower for kw in keywords)

def generate_improvements(resume_text, missing_skills=None, match_score=0):
    improvements = []
    text_lower = resume_text.lower()

    # ATS structure checks
    if not has_section(text_lower, ["summary", "objective", "profile", "about"]):
        improvements.append("Add a professional summary at the top — recruiters spend ~6 seconds scanning. A 2-3 sentence summary dramatically improves ATS ranking.")

    if not has_section(text_lower, ["skills", "technical skills", "core competencies", "technologies"]):
        improvements.append("Create a dedicated 'Technical Skills' section. ATS systems parse standalone skill lists most effectively.")

    if not has_section(text_lower, ["education", "degree", "university", "college", "bachelor", "master"]):
        improvements.append("Include an Education section with your degree, institution, and graduation year.")

    if not has_section(text_lower, ["experience", "work history", "employment", "positions"]):
        improvements.append("Add a Work Experience section with job titles, companies, dates, and bullet-pointed achievements.")

    # Missing skills
    if missing_skills and len(missing_skills) > 0:
        top_missing = missing_skills[:5]
        improvements.append(f"Add these missing skills to your resume if you have them: {', '.join(top_missing)}. These are specifically mentioned in the job description.")

    # Quantification check
    has_numbers = bool(re.search(r'\d+%|\d+\s*(million|billion|k\b|users|customers|revenue)', text_lower))
    if not has_numbers:
        improvements.append("Quantify your achievements! Use numbers like '→ Reduced load time by 40%', '→ Led a team of 8 engineers', '→ Increased sales by $200K'. Numbers make your resume stand out.")

    # Action verb check
    action_verbs = ["developed", "built", "implemented", "designed", "led", "managed", "created", "architected", "optimized", "delivered"]
    verb_count = sum(1 for v in action_verbs if v in text_lower)
    if verb_count < 3:
        improvements.append("Start bullet points with strong action verbs: Developed, Architected, Spearheaded, Delivered, Optimized, Engineered. This boosts readability and ATS scores.")

    # Length check
    word_count = len(resume_text.split())
    if word_count < 200:
        improvements.append("Your resume appears too short (under 200 words). Expand with more details about your projects, responsibilities, and accomplishments.")
    elif word_count > 800:
        improvements.append("Your resume may be too long. For most roles, 1 page (400-600 words) is ideal. Focus on the most relevant and impactful content.")

    # Contact info
    if not extract_email(resume_text):
        improvements.append("Add your professional email address — it's essential for recruiters to contact you.")

    if not extract_phone(resume_text):
        improvements.append("Include your phone number in the header section for easy recruiter access.")

    # Score-based general tips
    if match_score < 40:
        improvements.append("Your match score is below 40%. Consider tailoring this resume specifically for this job by mirroring exact phrases and keywords from the job description.")
    elif match_score < 60:
        improvements.append("Good foundation! To boost your match score, incorporate more keywords directly from the job posting into your experience bullet points.")

    # LinkedIn/portfolio
    if "linkedin" not in text_lower and "github" not in text_lower:
        improvements.append("Add links to LinkedIn and GitHub/Portfolio. Tech recruiters verify profiles frequently, and it signals professionalism.")

    return improvements[:8]  # Return top 8 improvements

@app.route('/', methods=['GET'])
def index():
    return jsonify({"status": "healthy", "service": "AI Resume Analyzer", "version": "2.0"})

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy", "service": "AI Resume Analyzer"})

@app.route('/analyze', methods=['POST'])
def analyze_resume():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    if not file.filename:
        return jsonify({"error": "Empty filename"}), 400

    filename = file.filename.lower()
    file_bytes = file.read()

    if filename.endswith('.pdf'):
        text = extract_text_from_pdf(file_bytes)
    elif filename.endswith('.docx'):
        text = extract_text_from_docx(file_bytes)
    else:
        return jsonify({"error": "Unsupported file type. Use PDF or DOCX"}), 400

    if not text:
        return jsonify({"error": "Could not extract text from file. The PDF may be image-based or corrupted."}), 400

    skills = extract_skills(text)
    email = extract_email(text)
    phone = extract_phone(text)
    experience_years = calculate_experience_years(text)
    word_count = len(text.split())
    improvements = generate_improvements(text)

    result = {
        "detected_skills": skills,
        "skills_count": len(skills),
        "email": email,
        "phone": phone,
        "estimated_experience_years": experience_years,
        "word_count": word_count,
        "improvements": improvements,
        "summary": f"Found {len(skills)} skills in resume with approximately {word_count} words."
    }

    logger.info(f"Analysis completed: {len(skills)} skills detected")
    return jsonify(result)

@app.route('/match', methods=['POST'])
def match_resume():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    job_description = request.form.get('job_description', '')
    if not job_description:
        return jsonify({"error": "Job description is required"}), 400

    file = request.files['file']
    if not file.filename:
        return jsonify({"error": "Empty filename"}), 400

    filename = file.filename.lower()
    file_bytes = file.read()

    if filename.endswith('.pdf'):
        resume_text = extract_text_from_pdf(file_bytes)
    elif filename.endswith('.docx'):
        resume_text = extract_text_from_docx(file_bytes)
    else:
        return jsonify({"error": "Unsupported file type"}), 400

    if not resume_text:
        return jsonify({"error": "Could not extract text from resume. The PDF may be image-based or corrupted."}), 400

    try:
        vectorizer = TfidfVectorizer(stop_words='english', ngram_range=(1, 2))
        tfidf_matrix = vectorizer.fit_transform([resume_text, job_description])
        similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0]
        match_score = round(float(similarity) * 100, 2)
    except Exception as e:
        logger.error(f"Similarity calculation error: {e}")
        match_score = 0.0

    resume_skills = set(extract_skills(resume_text))
    job_skills = set(extract_skills(job_description))

    matching_skills = list(resume_skills.intersection(job_skills))
    missing_skills = list(job_skills - resume_skills)
    extra_skills = list(resume_skills - job_skills)

    improvements = generate_improvements(resume_text, missing_skills=missing_skills, match_score=match_score)

    result = {
        "match_score": match_score,
        "match_percentage": f"{match_score}%",
        "detected_skills": list(resume_skills),
        "required_skills": list(job_skills),
        "matching_skills": matching_skills,
        "missing_skills": missing_skills,
        "extra_skills": extra_skills,
        "improvements": improvements,
        "recommendation": get_recommendation(match_score),
        "summary": f"Your resume matches {match_score}% of the job requirements."
    }

    logger.info(f"Match calculation completed: {match_score}%")
    return jsonify(result)

def get_recommendation(score):
    if score >= 80:
        return "Excellent match! Your profile aligns very well with this role."
    elif score >= 60:
        return "Good match! Consider highlighting more relevant skills."
    elif score >= 40:
        return "Moderate match. Work on acquiring the missing skills."
    else:
        return "Low match. Significant skill gap - consider upskilling before applying."

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
