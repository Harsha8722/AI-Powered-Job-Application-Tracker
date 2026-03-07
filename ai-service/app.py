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

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

# Load spaCy model
try:
    nlp = spacy.load("en_core_web_sm")
    logger.info("spaCy model loaded successfully")
except OSError:
    logger.warning("spaCy model not found. Run: python -m spacy download en_core_web_sm")
    nlp = None

# Common tech skills list for matching
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
    """Extract text from PDF file bytes."""
    try:
        reader = PyPDF2.PdfReader(io.BytesIO(file_bytes))
        text = ""
        for page in reader.pages:
            text += page.extract_text() + "\n"
        return text.strip()
    except Exception as e:
        logger.error(f"PDF extraction error: {e}")
        return ""

def extract_text_from_docx(file_bytes):
    """Extract text from DOCX file bytes."""
    try:
        doc = docx.Document(io.BytesIO(file_bytes))
        text = "\n".join([para.text for para in doc.paragraphs])
        return text.strip()
    except Exception as e:
        logger.error(f"DOCX extraction error: {e}")
        return ""

def extract_skills(text):
    """Extract skills from text using spaCy and keyword matching."""
    text_lower = text.lower()
    detected_skills = []

    # Match against known skills list
    for skill in TECH_SKILLS:
        if skill.lower() in text_lower:
            detected_skills.append(skill)

    # Use spaCy for additional entity extraction
    if nlp:
        doc = nlp(text[:5000])  # Limit text size for performance
        for ent in doc.ents:
            if ent.label_ in ["ORG", "PRODUCT", "WORK_OF_ART"]:
                skill_candidate = ent.text.lower()
                if len(skill_candidate) > 2 and skill_candidate not in detected_skills:
                    detected_skills.append(skill_candidate)

    return list(set(detected_skills))

def extract_email(text):
    """Extract email from text."""
    pattern = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b'
    emails = re.findall(pattern, text)
    return emails[0] if emails else None

def extract_phone(text):
    """Extract phone number from text."""
    pattern = r'[\+\(]?[1-9][0-9 .\-\(\)]{8,}[0-9]'
    phones = re.findall(pattern, text)
    return phones[0] if phones else None

def calculate_experience_years(text):
    """Rough estimate of years of experience."""
    pattern = r'(\d+)\+?\s*(?:years?|yrs?)'
    matches = re.findall(pattern, text.lower())
    if matches:
        return max(int(m) for m in matches)
    return 0

@app.route('/', methods=['GET'])
def index():
    return jsonify({"status": "healthy", "service": "AI Resume Analyzer", "message": "Welcome to AI Resume Analyzer API"})

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy", "service": "AI Resume Analyzer"})

@app.route('/analyze', methods=['POST'])
def analyze_resume():
    """Analyze a resume file and extract skills and keywords."""
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    filename = file.filename.lower()
    file_bytes = file.read()

    # Extract text based on file type
    if filename.endswith('.pdf'):
        text = extract_text_from_pdf(file_bytes)
    elif filename.endswith('.docx'):
        text = extract_text_from_docx(file_bytes)
    else:
        return jsonify({"error": "Unsupported file type. Use PDF or DOCX"}), 400

    if not text:
        return jsonify({"error": "Could not extract text from file"}), 400

    # Extract information
    skills = extract_skills(text)
    email = extract_email(text)
    phone = extract_phone(text)
    experience_years = calculate_experience_years(text)

    # Word count stats
    word_count = len(text.split())

    result = {
        "detected_skills": skills,
        "skills_count": len(skills),
        "email": email,
        "phone": phone,
        "estimated_experience_years": experience_years,
        "word_count": word_count,
        "summary": f"Found {len(skills)} skills in resume with approximately {word_count} words."
    }

    logger.info(f"Analysis completed: {len(skills)} skills detected")
    return jsonify(result)

@app.route('/match', methods=['POST'])
def match_resume():
    """Match a resume against a job description."""
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    job_description = request.form.get('job_description', '')
    if not job_description:
        return jsonify({"error": "Job description is required"}), 400

    file = request.files['file']
    filename = file.filename.lower()
    file_bytes = file.read()

    # Extract resume text
    if filename.endswith('.pdf'):
        resume_text = extract_text_from_pdf(file_bytes)
    elif filename.endswith('.docx'):
        resume_text = extract_text_from_docx(file_bytes)
    else:
        return jsonify({"error": "Unsupported file type"}), 400

    if not resume_text:
        return jsonify({"error": "Could not extract text from resume"}), 400

    # Calculate TF-IDF cosine similarity
    try:
        vectorizer = TfidfVectorizer(stop_words='english', ngram_range=(1, 2))
        tfidf_matrix = vectorizer.fit_transform([resume_text, job_description])
        similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0]
        match_score = round(float(similarity) * 100, 2)
    except Exception as e:
        logger.error(f"Similarity calculation error: {e}")
        match_score = 0.0

    # Extract skills from both documents
    resume_skills = set(extract_skills(resume_text))
    job_skills = set(extract_skills(job_description))

    # Find matching and missing skills
    matching_skills = list(resume_skills.intersection(job_skills))
    missing_skills = list(job_skills - resume_skills)
    extra_skills = list(resume_skills - job_skills)

    result = {
        "match_score": match_score,
        "match_percentage": f"{match_score}%",
        "detected_skills": list(resume_skills),
        "required_skills": list(job_skills),
        "matching_skills": matching_skills,
        "missing_skills": missing_skills,
        "extra_skills": extra_skills,
        "recommendation": get_recommendation(match_score),
        "summary": f"Your resume matches {match_score}% of the job requirements."
    }

    logger.info(f"Match calculation completed: {match_score}%")
    return jsonify(result)

def get_recommendation(score):
    """Get recommendation based on match score."""
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
