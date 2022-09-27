package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;

public interface QuizService {

	List<QuizResponseDto> getAllQuizzes();

	QuestionResponseDto getRandomQuestion(Long quizId);

	QuizResponseDto createQuiz(QuizResponseDto quizResponseDto);

	QuizResponseDto renameQuiz(Long quizId, String newQuizName);

	QuizResponseDto addQuestionToQuiz(Long quizId, Question question);

	QuizResponseDto deleteQuizById(Long quizId);

	QuestionResponseDto deleteQuestion(Long quizId, Long questionId);

}
