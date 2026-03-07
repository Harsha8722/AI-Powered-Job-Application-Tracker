package com.jobtracker.config;

import com.jobtracker.model.JobApplication;
import com.jobtracker.model.User;
import com.jobtracker.repository.JobRepository;
import com.jobtracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      JobRepository jobRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Only seed if db is empty (first run)
            if (userRepository.count() > 0) return;

            // Create a demo user
            User demo = new User();
            demo.setName("Demo User");
            demo.setEmail("demo@jobtracker.com");
            demo.setPassword(passwordEncoder.encode("Demo@1234"));
            demo = userRepository.save(demo);

            // Sample job applications
            List<Object[]> sampleJobs = List.of(
                new Object[]{"Google",       "Software Engineer III",      "INTERVIEW", "2026-02-10", "2026-02-25", "Reached L5 interview. Focus on system design."},
                new Object[]{"Amazon",       "Backend Developer",          "APPLIED",   "2026-02-15", null,         "Applied via LinkedIn. Referral from John."},
                new Object[]{"Microsoft",    "Senior Java Engineer",       "OFFER",     "2026-01-20", "2026-02-05", "Received offer: $145k + RSU. Deadline 03/15."},
                new Object[]{"Meta",         "Full Stack Engineer",        "REJECTED",  "2026-01-30", "2026-02-12", "Rejected after technical screen."},
                new Object[]{"Netflix",      "Data Engineer",              "APPLIED",   "2026-03-01", null,         "Submitted via careers portal."},
                new Object[]{"Stripe",       "Platform Engineer",          "INTERVIEW", "2026-02-18", "2026-03-10", "Phone screen passed. Onsite scheduled."},
                new Object[]{"Atlassian",    "Backend Engineer - Java",    "APPLIED",   "2026-02-28", null,         "Dream company. Applied to 3 teams."},
                new Object[]{"Apple",        "iOS/Android Developer",      "REJECTED",  "2026-01-25", "2026-02-08", "Good feedback but no headcount at this time."},
                new Object[]{"OpenAI",       "ML Infrastructure Engineer", "INTERVIEW", "2026-03-02", "2026-03-15", "Very interesting role. AI/ML infra focus."},
                new Object[]{"Salesforce",   "Cloud Engineer",             "OFFER",     "2026-02-05", "2026-02-20", "Offer: $130k. Evaluating vs Microsoft."}
            );

            final User finalDemo = demo;
            sampleJobs.forEach(job -> {
                JobApplication ja = new JobApplication();
                ja.setUser(finalDemo);
                ja.setCompany((String) job[0]);
                ja.setRole((String) job[1]);
                ja.setStatus(JobApplication.ApplicationStatus.valueOf((String) job[2]));
                ja.setApplicationDate(LocalDate.parse((String) job[3]));
                if (job[4] != null) ja.setInterviewDate(LocalDate.parse((String) job[4]));
                ja.setNotes((String) job[5]);
                jobRepository.save(ja);
            });

            System.out.println("✅ Demo data seeded! Login: demo@jobtracker.com / Demo@1234");
        };
    }
}
