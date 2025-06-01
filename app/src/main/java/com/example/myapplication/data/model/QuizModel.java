package com.example.myapplication.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class QuizModel {
    private List<TrafficSign> allTrafficSign;
    private List<TrafficSign> currentTrafficSign;
    private int currentIndex;
    private int score;
    private Random random;
    private double currentDefToTermRatio;

    private QuestionData curQuestionData;

    public QuizModel(List<TrafficSign> allTrafficSign) {
        this.allTrafficSign = new ArrayList<>();
        this.allTrafficSign.addAll(allTrafficSign);

        random = new Random();
        resetQuiz(0, "All", 0.);
    }

    public  void resetQuiz(int numberQuestions, String type, double ratio) {
        List<TrafficSign> filteredTrafficSign = new ArrayList<>();
        this.currentDefToTermRatio = ratio;

        if ("All".equalsIgnoreCase(type)) {
            filteredTrafficSign.addAll(allTrafficSign);
        } else {
            // Lọc sign theo type
            filteredTrafficSign = allTrafficSign.stream().filter(trafficSign -> trafficSign.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        Collections.shuffle(filteredTrafficSign); // Xáo trộn danh sách

        if (numberQuestions > 0 && numberQuestions < filteredTrafficSign.size()) {
            currentTrafficSign = new ArrayList<>(filteredTrafficSign.subList(0, numberQuestions));
        } else {
            currentTrafficSign = new ArrayList<>(filteredTrafficSign);
        }

        currentIndex = 0;
        score = 0;
        curQuestionData = null;
    }

    public QuestionData getCurQuestionData() {
        if (currentIndex >= currentTrafficSign.size()) {
            return null;
        }

        TrafficSign curTrafficSign = currentTrafficSign.get(currentIndex);
        TrafficSign.QuestionType type = getRandomQuestionType(curTrafficSign);

        String questionText = null;
        String correctTerm = null;
        List<String> options = new ArrayList<>();

        switch (type) {
            case DEFINITION_TO_TERM:
                questionText = curTrafficSign.getDescription();
                correctTerm = curTrafficSign.getName();
                options = generateOptionsForTerm(correctTerm);
                break;
            case TERM_TO_DEFINITION:
                questionText = curTrafficSign.getName();
                correctTerm = curTrafficSign.getDescription();
                options = generateOptionsForDefinition(correctTerm);
                break;
            case IMAGE_TO_TERM:
                questionText = curTrafficSign.getImage();
                correctTerm = curTrafficSign.getName();
                options = generateOptionsForDefinition(correctTerm);
                break;
        }

        curQuestionData = new QuestionData(type, questionText, correctTerm, options);
        return curQuestionData;
    }

    private TrafficSign.QuestionType getRandomQuestionType(TrafficSign word) {
        double rand = random.nextDouble();

        // Trường hợp đặc biệt: Chỉ ảnh (-1.0) or chỉ Định nghĩa (1.0).
        if (currentDefToTermRatio == -1.0) { // -1.0: "Chỉ Hình ảnh -> Tên"
            return TrafficSign.QuestionType.IMAGE_TO_TERM;
        } else if (currentDefToTermRatio == 1.0) { // V1.0: "Chỉ Định nghĩa -> Tên"
            return TrafficSign.QuestionType.DEFINITION_TO_TERM;
        } else if (currentDefToTermRatio == 0.0) { // V0.0: "Chỉ Tên -> Định nghĩa"
            return TrafficSign.QuestionType.TERM_TO_DEFINITION;
        }

        // Logic mặc định khi tỉ lệ không đặc biệt (ví dụ: 0.5 cho kết hợp)
        // Chia 1.0 thành 2 phần: DEF_TO_TERM, (chia đều TERM_TO_DEFINITION, IMAGE_TO_TERM)
        double remainingRatio = 1.0 - currentDefToTermRatio;
        double imageToTermRatio = 0;
        double termToDefRatio = 0;

        // Chia đều phần còn lại giữa IMAGE_TO_TERM và TERM_TO_DEFINITION
        imageToTermRatio = remainingRatio / 2;
        termToDefRatio = remainingRatio / 2;


        if (rand < currentDefToTermRatio) {
            return TrafficSign.QuestionType.DEFINITION_TO_TERM;
        } else if (rand < currentDefToTermRatio + imageToTermRatio) { // Ngưỡng cho IMAGE_TO_TERM
            return TrafficSign.QuestionType.IMAGE_TO_TERM;
        } else { // Còn lại là TERM_TO_DEFINITION
            return TrafficSign.QuestionType.TERM_TO_DEFINITION;
        }
    }

    // Tạo các lựa chọn là TÊN biển báo
    private List<String> generateOptionsForTerm(String correct) {
        List<String> options = new ArrayList<>();
        options.add(correct);

        List<TrafficSign> wrongTrafficSign = new ArrayList<>(allTrafficSign);
        wrongTrafficSign.removeIf(trafficSign -> trafficSign.getName().equals(correct));

        while (options.size() < 4 && !wrongTrafficSign.isEmpty()) {
            int randomIndex = random.nextInt(wrongTrafficSign.size());
            String wrongTerm = wrongTrafficSign.get(randomIndex).getName();
            if (!options.contains(wrongTerm)) {
                options.add(wrongTerm);
            }
            wrongTrafficSign.remove(randomIndex);
        }
        Collections.shuffle(options);
        return options;
    }

    // Tạo lựa chọn ĐỊNH NGHĨA
    private List<String> generateOptionsForDefinition(String correct) {
        List<String> options = new ArrayList<>();
        options.add(correct);

        List<TrafficSign> wrongTrafficSign = new ArrayList<>(allTrafficSign);
        wrongTrafficSign.removeIf(trafficSign -> trafficSign.getName().equals(correct));

        while (options.size() < 4 && !wrongTrafficSign.isEmpty()) {
            int randomIndex = random.nextInt(wrongTrafficSign.size());
            String wrongDef = wrongTrafficSign.get(randomIndex).getDescription();
            if (!options.contains(wrongDef)) {
                options.add(wrongDef);
            }
            wrongTrafficSign.remove(randomIndex);
        }
        Collections.shuffle(options);
        return options;
    }

    // Tạo lựa chọn HÌNH ẢNH
    private List<String> generateOptionsForImage(String correct) {
        List<String> options = new ArrayList<>();
        options.add(correct);

        List<TrafficSign> wrongTrafficSign = new ArrayList<>(allTrafficSign);
        wrongTrafficSign.removeIf(trafficSign -> trafficSign.getName().equals(correct));

        while (options.size() < 4 && !wrongTrafficSign.isEmpty()) {
            int randomIndex = random.nextInt(wrongTrafficSign.size());
            String wrongImage = wrongTrafficSign.get(randomIndex).getImage();
            if (!options.contains(wrongImage)) {
                options.add(wrongImage);
            }
            wrongTrafficSign.remove(randomIndex);
        }
        Collections.shuffle(options);
        return options;
    }

    // Kiểm tra đáp án
    public boolean checkAnswer(String selectedAnswer) {
        if (curQuestionData == null) return false;
        boolean isCorrect = selectedAnswer.equals(curQuestionData.getAnswer()); // correctTerm chứa đáp án đúng (tên hoặc định nghĩa)
        if (isCorrect) {
            score++;
        }
        return isCorrect;
    }

    public void moveToNextQuestion() {
        currentIndex++;
    }

    public boolean hasNextQuestion() {
        return currentIndex < currentTrafficSign.size();
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return currentTrafficSign.size();
    }

    public List<String> getAvailableType() {
        List<String> languages = allTrafficSign.stream()
                .map(TrafficSign::getType)
                .distinct()
                .collect(Collectors.toList());
        languages.add(0, "All");
        return languages;
    }
}
