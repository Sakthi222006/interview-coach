package com.interviewcoach.backend.ai;

import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.model.Question;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildEvaluationPrompt(Question question, String candidateAnswer) {
        String answer = (candidateAnswer == null || candidateAnswer.isBlank())
            ? "[Candidate did not provide an answer]"
            : candidateAnswer;

        return """
            You are a Senior Software Engineer and Technical Interviewer at a top tech company.
            You are evaluating a candidate's answer during a mock technical interview.

            INTERVIEW CONTEXT:
            Topic: %s
            Difficulty: %s
            Question: %s

            CANDIDATE'S ANSWER:
            %s

            EVALUATION INSTRUCTIONS:
            Evaluate across four dimensions (0-100 each):
            1. technicalAccuracy  — Is the information factually correct and precise?
            2. communication      — Is the answer clear, well-structured, and articulate?
            3. problemSolving     — Does it demonstrate logical thinking and good approach?
            4. confidence         — Does it sound assured, well-reasoned, and complete?

            SCORING RULES:
            - Empty or single-word answers score below 20
            - Partial answers receive partial credit
            - overallScore = weighted average (technicalAccuracy*0.4 + communication*0.2 + problemSolving*0.3 + confidence*0.1)
            - Round overallScore to nearest integer
            - Be honest but constructive — like a great mentor

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no explanation outside the JSON
            - strengths: 2-3 specific things the candidate did well
            - improvements: 2-3 specific, actionable improvements
            - missingConcepts: key technical concepts not mentioned (max 4)
            - modelAnswer: ideal answer in 2-4 sentences
            - interviewerFeedback: conversational paragraph (3-5 sentences) as a real interviewer would say

            REQUIRED JSON FORMAT:
            {
              "overallScore": <integer 0-100>,
              "technicalAccuracy": <integer 0-100>,
              "communication": <integer 0-100>,
              "problemSolving": <integer 0-100>,
              "confidence": <integer 0-100>,
              "strengths": ["<specific point>", "<specific point>"],
              "improvements": ["<actionable point>", "<actionable point>"],
              "missingConcepts": ["<concept>", "<concept>"],
              "modelAnswer": "<2-4 sentence ideal answer>",
              "interviewerFeedback": "<3-5 sentence conversational feedback>"
            }
            """.formatted(
                question.getTopic(),
                question.getDifficulty(),
                question.getQuestionText(),
                answer
            );
    }

    public String buildStarEvaluationPrompt(Question question, String candidateAnswer) {
        String answer = (candidateAnswer == null || candidateAnswer.isBlank())
            ? "[Candidate did not provide an answer]"
            : candidateAnswer;

        return """
            You are evaluating a behavioural interview answer using the STAR method.

            Question: %s
            Candidate Answer: %s

            Score each STAR component (0-25 each, total must equal sum of four):
              Situation (0-25): Did they clearly set the context?
              Task      (0-25): Did they describe their specific responsibility?
              Action    (0-25): Did they explain concrete steps THEY personally took?
              Result    (0-25): Did they share a measurable or meaningful outcome?

            SCORING RULES:
              - Missing component entirely = 0
              - Vague mention = 1-10
              - Clear but incomplete = 11-18
              - Clear, specific, and well-articulated = 19-25
              - Empty answer = all zeros

            RESPOND WITH ONLY THIS JSON (no markdown, no extra text):
            {
              "starSituationScore": <0-25>,
              "starTaskScore":      <0-25>,
              "starActionScore":    <0-25>,
              "starResultScore":    <0-25>,
              "starTotalScore":     <sum of above>
            }
            """.formatted(question.getQuestionText(), answer);
    }

    public String buildResumeExtractionPrompt(String resumeText) {
        String text = (resumeText == null || resumeText.isBlank())
            ? "[Resume text could not be extracted]"
            : resumeText;

        return """
            You are a professional resume analyst and technical recruiter.
            Extract structured information from the following resume:

            RESUME TEXT:
            %s

            EXTRACTION INSTRUCTIONS:
            Analyze the resume and extract all technical and professional information.

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no explanation outside the JSON
            - Each list should contain relevant items found in the resume
            - If a section is not found, use an empty list []
            - resumeScore: overall quality score (0-100) based on clarity, structure, and completeness
            - confidenceScore: your confidence in the extraction accuracy (0.0-1.0)

            REQUIRED JSON FORMAT:
            {
              "skills": ["<skill>", "<skill>"],
              "technologies": ["<technology>", "<technology>"],
              "frameworks": ["<framework>", "<framework>"],
              "databases": ["<database>", "<database>"],
              "tools": ["<tool>", "<tool>"],
              "projects": ["<project description>", "<project description>"],
              "domains": ["<domain/industry>", "<domain/industry>"],
              "strengths": ["<identified strength>", "<identified strength>"],
              "weaknesses": ["<identified gap or weakness>", "<identified gap or weakness>"],
              "recommendedRoles": ["<recommended job title>", "<recommended job title>"],
              "resumeScore": <integer 0-100>,
              "confidenceScore": <decimal 0.0-1.0>
            }
            """.formatted(text);
    }

    public String buildResumeQuestionPrompt(ResumeExtractionResult analysis, String difficulty, int questionCount) {
        String skills = listToString(analysis.getSkills());
        String projects = listToString(analysis.getProjects());
        String technologies = listToString(analysis.getTechnologies());
        String frameworks = listToString(analysis.getFrameworks());
        String domains = listToString(analysis.getDomains());

        return """
            You are an expert technical interviewer preparing resume-grounded interview questions.
            Use the candidate's resume analysis and focus on real skills, projects, technologies, and domains.

            DIFFICULTY: %s
            QUESTION COUNT: %d

            RESUME ANALYSIS:
            Skills: %s
            Projects: %s
            Technologies: %s
            Frameworks: %s
            Domains: %s

            GENERATION RULES:
            - Generate exactly %d interview questions at the requested difficulty.
            - Use the resume skills and projects to ground each question in the candidate's experience.
            - Do not invent resume details that are not present in the analysis.
            - Keep questions concise, clear, and relevant.
            - Favor technical and role-aligned interview questions.

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no extra explanation
            - Return an object with a single "questions" array
            - Each question must be a single string

            REQUIRED JSON FORMAT:
            {
              "questions": ["<question 1>", "<question 2>", "..."]
            }
            """.formatted(
                difficulty,
                questionCount,
                skills,
                projects,
                technologies,
                frameworks,
                domains,
                questionCount
            );
    }

    public String buildCareerRoadmapPrompt(java.util.List<String> resumeSkills,
                                          java.util.List<String> missingConcepts,
                                          java.util.List<String> weakTopics,
                                          String analyticsSummary,
                                          String targetRole) {

        String skills = (resumeSkills == null || resumeSkills.isEmpty()) ? "None" : String.join(", ", resumeSkills);
        String missing = (missingConcepts == null || missingConcepts.isEmpty()) ? "None" : String.join(", ", missingConcepts);
        String weak = (weakTopics == null || weakTopics.isEmpty()) ? "None" : String.join(", ", weakTopics);
        String analytics = (analyticsSummary == null || analyticsSummary.isBlank()) ? "No analytics provided" : analyticsSummary;
        String role = (targetRole == null || targetRole.isBlank()) ? "Target role not specified" : targetRole;

        return """
            You are an expert career coach and technical mentor. Build a personalized career roadmap for the candidate.

            INPUT:
            - Resume skills: %s
            - Missing concepts: %s
            - Weak topics: %s
            - Analytics summary: %s
            - Target role: %s

            GOAL:
            Produce a JSON-only output that contains three phases: "7-Day Plan", "30-Day Plan", and "90-Day Plan".
            Each phase must be an object with: title, description, durationDays, priority, difficulty, estimatedHours, and tasks (an array).
            Each task must have: title, description, durationDays, priority, difficulty, estimatedHours.

            CONTENT GUIDELINES:
            - Use resume skills to prioritize tasks and resources.
            - Address missing concepts and weak topics explicitly.
            - Include practice goals, mock interviews, and project recommendations where relevant.
            - Recommend specific resources (courses, docs, platforms) inside task descriptions.
            - Keep responses concise and actionable.

            OUTPUT RULES:
            - Respond with ONLY valid JSON, no markdown or prose around it.
            - Top-level JSON format:
            {
              "overallReadiness": "<LOW|MEDIUM|HIGH>",
              "readinessScore": <0-100>,
              "phases": [ { <phase objects> } ]
            }

            Be specific, use numbers for durations and estimates, and keep the entire object parseable.
            """.formatted(skills, missing, weak, analytics, role);
    }

    public String buildResumeMatchPrompt(ResumeExtractionResult analysis,
                                         String targetRole,
                                         String jobDescription,
                                         java.util.List<String> desiredSkills) {
        String skills = listToString(analysis.getSkills());
        String technologies = listToString(analysis.getTechnologies());
        String projects = listToString(analysis.getProjects());
        String frameworks = listToString(analysis.getFrameworks());
        String domains = listToString(analysis.getDomains());
        String strengths = listToString(analysis.getStrengths());
        String weaknesses = listToString(analysis.getWeaknesses());
        String role = (targetRole == null || targetRole.isBlank()) ? "Not specified" : targetRole;
        String description = (jobDescription == null || jobDescription.isBlank()) ? "No job description provided." : jobDescription;
        String desired = (desiredSkills == null || desiredSkills.isEmpty()) ? "None" : String.join(", ", desiredSkills);

        return """
            You are a talent matching specialist for technical roles.
            Use the resume analysis and the target role/job description to estimate the candidate's fit.

            RESUME ANALYSIS:
            Skills: %s
            Technologies: %s
            Projects: %s
            Frameworks: %s
            Domains: %s
            Strengths: %s
            Weaknesses: %s

            ROLE CONTEXT:
            Target role: %s
            Desired skills: %s
            Job description: %s

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no explanation outside the JSON
            - Use the resume skills and role context to estimate fit

            REQUIRED JSON FORMAT:
            {
              "matchedRole": "<best-fit role title>",
              "matchScore": <0-100>,
              "roleFitSummary": "<short summary of fit>",
              "recommendedSkills": ["<skill>", "<skill>"],
              "matchHighlights": ["<point>", "<point>"],
              "suggestedGaps": ["<gap>", "<gap>"]
            }
            """.formatted(
                skills,
                technologies,
                projects,
                frameworks,
                domains,
                strengths,
                weaknesses,
                role,
                desired,
                description
            );
    }

    public String buildResumeReadinessPrompt(ResumeExtractionResult analysis, String targetRole) {
        String skills = listToString(analysis.getSkills());
        String projects = listToString(analysis.getProjects());
        String strengths = listToString(analysis.getStrengths());
        String weaknesses = listToString(analysis.getWeaknesses());
        String role = (targetRole == null || targetRole.isBlank()) ? "Not specified" : targetRole;

        return """
            You are a career readiness advisor.
            Assess the candidate's resume readiness for applying to the target role.

            RESUME ANALYSIS:
            Skills: %s
            Projects: %s
            Strengths: %s
            Weaknesses: %s
            Target role: %s

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no explanation outside the JSON

            REQUIRED JSON FORMAT:
            {
              "overallReadiness": "<LOW|MEDIUM|HIGH>",
              "readinessScore": <0-100>,
              "keyStrengths": ["<strength>", "<strength>"],
              "improvementAreas": ["<area>", "<area>"],
              "nextSteps": ["<action>", "<action>"]
            }
            """.formatted(skills, projects, strengths, weaknesses, role);
    }

    public String buildVoiceInterviewPrompt(
            String topic,
            String targetRole,
            String difficulty,
            String questionText,
            String transcript,
            java.util.List<String> conversationHistory
    ) {
        String question = (questionText == null || questionText.isBlank()) ? "No specific question provided." : questionText;
        String history = (conversationHistory == null || conversationHistory.isEmpty())
            ? "No prior spoken context."
            : String.join("\n", conversationHistory);
        String answer = (transcript == null || transcript.isBlank()) ? "[No transcript available]" : transcript;

        return """
            You are a recruiter conducting a voice interview.
            Evaluate the candidate's spoken answer using the following criteria:
            - confidence
            - clarity
            - communication
            - grammar
            - technicalQuality
            - STAR structure
            - completeness
            - speaking pace
            - filler words

            INTERVIEW CONTEXT:
            Topic: %s
            Target role: %s
            Difficulty: %s
            Question: %s

            PRIOR CONVERSATION:
            %s

            CANDIDATE TRANSCRIPT:
            %s

            INSTRUCTIONS:
            1. Provide a numeric score for each metric between 0 and 100.
            2. Estimate overall performance as a weighted average.
            3. Identify specific strengths, improvements, and missing points.
            4. Offer one concise follow-up interview question.

            OUTPUT RULES:
            - Respond with ONLY valid JSON
            - No markdown, no code fences, no explanation outside the JSON
            - Use arrays for text items

            REQUIRED JSON FORMAT:
            {
              "overallScore": <0-100>,
              "confidence": <0-100>,
              "clarity": <0-100>,
              "communication": <0-100>,
              "grammar": <0-100>,
              "technicalQuality": <0-100>,
              "starScore": <0-100>,
              "completeness": <0-100>,
              "strengths": ["<strength>", "<strength>"],
              "improvements": ["<improvement>", "<improvement>"],
              "missingPoints": ["<point>", "<point>"],
              "modelAnswer": "<2-4 sentence ideal response>",
              "interviewerFeedback": "<3-5 sentence conversational feedback>",
              "nextQuestion": "<follow-up question>"
            }
            """.formatted(topic, targetRole, difficulty, question, history, answer);
    }

    private String listToString(java.util.List<String> values) {
        return (values == null || values.isEmpty()) ? "None" : String.join(", ", values);
    }
}
