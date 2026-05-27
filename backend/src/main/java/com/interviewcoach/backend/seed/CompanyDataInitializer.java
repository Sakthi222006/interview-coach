package com.interviewcoach.backend.seed;

import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyDataInitializer {

    private final CompanyProfileRepository companyProfileRepository;
    private final AptitudeQuestionRepository aptitudeQuestionRepository;
    private final CodingChallengeRepository codingChallengeRepository;

    @PostConstruct
    public void init() {
        if (companyProfileRepository.count() > 0) {
            return;
        }

        List<CompanyProfile> profiles = new ArrayList<>();

        profiles.add(CompanyProfile.builder()
                .companyName("TCS")
                .description("Tata Consultancy Services is a global IT services and consulting leader.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(30.0)
                .codingWeightage(30.0)
                .communicationWeightage(25.0)
                .technicalWeightage(15.0)
                .focusTechnologies("[\"Java\", \"SQL\", \"OOP\", \"Data Structures\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Amazon")
                .description("Amazon focuses on customer obsession, operational excellence and deep technical ownership.")
                .difficulty(CompanyProfile.DifficultyLevel.HARD)
                .hiringPattern("Coding → System Design → Behavioral")
                .interviewRounds("[\"Coding\", \"Design\", \"Leadership\", \"HR\"]")
                .aptitudeWeightage(20.0)
                .codingWeightage(35.0)
                .communicationWeightage(20.0)
                .technicalWeightage(25.0)
                .focusTechnologies("[\"Algorithms\", \"Data Structures\", \"Distributed Systems\", \"AWS\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Google")
                .description("Google is known for deep technical interviews and strong system design evaluation.")
                .difficulty(CompanyProfile.DifficultyLevel.HARD)
                .hiringPattern("Coding → System Design → Behavioral")
                .interviewRounds("[\"Coding\", \"Design\", \"Behavioral\"]")
                .aptitudeWeightage(15.0)
                .codingWeightage(40.0)
                .communicationWeightage(20.0)
                .technicalWeightage(25.0)
                .focusTechnologies("[\"Algorithms\", \"Graphs\", \"Design Patterns\", \"Python\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Microsoft")
                .description("Microsoft interviews balance coding, design, and product sense.")
                .difficulty(CompanyProfile.DifficultyLevel.HARD)
                .hiringPattern("Coding → Design → HR")
                .interviewRounds("[\"Coding\", \"Design\", \"Behavioral\"]")
                .aptitudeWeightage(20.0)
                .codingWeightage(35.0)
                .communicationWeightage(25.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"C#\", \"System Design\", \"Cloud\", \"Data Structures\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Infosys")
                .description("Infosys evaluates aptitude, technical fundamentals and communication skills.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(35.0)
                .codingWeightage(25.0)
                .communicationWeightage(20.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"Java\", \"SQL\", \"Basics\", \"Communication\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Wipro")
                .description("Wipro evaluates logical reasoning, coding fundamentals and cultural fit.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(30.0)
                .codingWeightage(30.0)
                .communicationWeightage(20.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"Java\", \"Problem Solving\", \"Communication\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Accenture")
                .description("Accenture focuses on aptitude, consulting mindset, and technical proficiency.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"Behavioral\"]")
                .aptitudeWeightage(30.0)
                .codingWeightage(25.0)
                .communicationWeightage(25.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"Consulting\", \"Java\", \"Cloud\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Cognizant")
                .description("Cognizant interviews target aptitude, programming fundamentals, and HR readiness.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(30.0)
                .codingWeightage(30.0)
                .communicationWeightage(20.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"Java\", \"SQL\", \"Data Structures\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Capgemini")
                .description("Capgemini combines aptitude screening with technical and communication evaluation.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(30.0)
                .codingWeightage(30.0)
                .communicationWeightage(25.0)
                .technicalWeightage(15.0)
                .focusTechnologies("[\"Java\", \"Problem Solving\", \"Communication\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Zoho")
                .description("Zoho focuses on strong technical fundamentals, problem-solving and product sense.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"HR\"]")
                .aptitudeWeightage(25.0)
                .codingWeightage(35.0)
                .communicationWeightage(20.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"Python\", \"Algorithms\", \"Databases\"]")
                .build());

        profiles.add(CompanyProfile.builder()
                .companyName("Freshworks")
                .description("Freshworks blends product fit and technical readiness for its interview tracks.")
                .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                .hiringPattern("Aptitude → Technical → HR")
                .interviewRounds("[\"Aptitude\", \"Technical\", \"Behavioral\"]")
                .aptitudeWeightage(25.0)
                .codingWeightage(30.0)
                .communicationWeightage(25.0)
                .technicalWeightage(20.0)
                .focusTechnologies("[\"JavaScript\", \"React\", \"APIs\"]")
                .build());

        List<CompanyProfile> savedProfiles = companyProfileRepository.saveAll(profiles);
        seedQuestionsAndChallenges(savedProfiles);
    }

    private void seedQuestionsAndChallenges(List<CompanyProfile> profiles) {
        for (CompanyProfile profile : profiles) {
            if (profile.getCompanyName().equalsIgnoreCase("TCS")) {
                aptitudeQuestionRepository.save(AptitudeQuestion.builder()
                        .company(profile)
                        .question("What is the next number in the series: 2, 6, 12, 20, ?")
                        .category(AptitudeQuestion.Category.QUANTITATIVE)
                        .difficulty(CompanyProfile.DifficultyLevel.EASY)
                        .optionA("26")
                        .optionB("28")
                        .optionC("30")
                        .optionD("32")
                        .correctAnswer("B")
                        .explanation("The series uses n*(n+1): 2, 6, 12, 20, 30.")
                        .timeLimit(90)
                        .topic("Number Series")
                        .build());
                codingChallengeRepository.save(CodingChallenge.builder()
                        .company(profile)
                        .title("Array Pair Sum")
                        .description("Given an array of integers, find whether there exists a pair with sum equal to a given target.")
                        .difficulty(CompanyProfile.DifficultyLevel.EASY)
                        .topic("Arrays")
                        .exampleInput("[1, 4, 5, 7], target=9")
                        .exampleOutput("true")
                        .constraints("2 <= size <= 1000")
                        .testCases("[{\"input\":\"[1,4,5,7],9\",\"output\":\"true\"},{\"input\":\"[1,2,3],6\",\"output\":\"false\"}]")
                        .solutionApproach("Use a hash set to track complements.")
                        .acceptanceRate(68)
                        .relatedTopics("[\"Hashing\", \"Arrays\"]")
                        .timeLimit(60)
                        .memoryLimit(256)
                        .build());
            }
            if (profile.getCompanyName().equalsIgnoreCase("Amazon")) {
                aptitudeQuestionRepository.save(AptitudeQuestion.builder()
                        .company(profile)
                        .question("If 3 machines take 3 minutes to make 3 widgets, how many minutes do 100 machines take to make 100 widgets?")
                        .category(AptitudeQuestion.Category.LOGICAL_REASONING)
                        .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                        .optionA("1")
                        .optionB("3")
                        .optionC("100")
                        .optionD("300")
                        .correctAnswer("B")
                        .explanation("Each widget takes 3 minutes on a machine.")
                        .timeLimit(90)
                        .topic("Rate Problems")
                        .build());
                codingChallengeRepository.save(CodingChallenge.builder()
                        .company(profile)
                        .title("Unique Character Count")
                        .description("Calculate the number of unique characters in a string.")
                        .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                        .topic("Strings")
                        .exampleInput("hello")
                        .exampleOutput("4")
                        .constraints("1 <= length <= 1000")
                        .testCases("[{\"input\":\"hello\",\"output\":\"4\"},{\"input\":\"amazon\",\"output\":\"5\"}]")
                        .solutionApproach("Use a set to count distinct characters.")
                        .acceptanceRate(55)
                        .relatedTopics("[\"Strings\", \"HashSet\"]")
                        .timeLimit(90)
                        .memoryLimit(256)
                        .build());
            }
            if (profile.getCompanyName().equalsIgnoreCase("Google")) {
                aptitudeQuestionRepository.save(AptitudeQuestion.builder()
                        .company(profile)
                        .question("Which traversal will print nodes of a binary tree in sorted order?")
                        .category(AptitudeQuestion.Category.DATA_INTERPRETATION)
                        .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                        .optionA("Pre-order")
                        .optionB("In-order")
                        .optionC("Post-order")
                        .optionD("Level-order")
                        .correctAnswer("B")
                        .explanation("In-order traversal of a BST prints nodes in sorted order.")
                        .timeLimit(90)
                        .topic("Trees")
                        .build());
                codingChallengeRepository.save(CodingChallenge.builder()
                        .company(profile)
                        .title("Two Sum")
                        .description("Return true if any two numbers in the array add up to the target.")
                        .difficulty(CompanyProfile.DifficultyLevel.MEDIUM)
                        .topic("Arrays")
                        .exampleInput("[2,7,11,15], target=9")
                        .exampleOutput("true")
                        .constraints("2 <= nums.length <= 10000")
                        .testCases("[{\"input\":\"[2,7,11,15],9\",\"output\":\"true\"},{\"input\":\"[1,2,3],6\",\"output\":\"false\"}]")
                        .solutionApproach("Use a map to find complements in one pass.")
                        .acceptanceRate(50)
                        .relatedTopics("[\"HashMap\", \"Arrays\"]")
                        .timeLimit(90)
                        .memoryLimit(256)
                        .build());
            }
        }
    }
}
