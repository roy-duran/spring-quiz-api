package com.cooksys.quiz_api.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.BadRequestException;
import com.cooksys.quiz_api.exceptions.NotFoundException;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuizMapper quizMapper;
	private final QuestionRepository questionRepository;
	private final QuestionMapper questionMapper;
	private final AnswerRepository answerRepository;

	private Quiz getQuiz(Long id) {
		Optional<Quiz> optionalQuiz = quizRepository.findByIdAndDeletedFalse(id);
		if (optionalQuiz.isEmpty()) {
			throw new NotFoundException("No quiz found with id: " + id);
		}
		List<Question> questions = optionalQuiz.get().getQuestions();
		questions.removeIf(Question::isDeleted);
		Quiz resultQuiz = optionalQuiz.get();
		resultQuiz.setQuestions(questions);
		return resultQuiz;
	}

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		List<Quiz> allQuizzes = quizRepository.findAllByDeletedFalse();
		for (Quiz quiz : allQuizzes) {
			List<Question> questionList = quiz.getQuestions();
			questionList.removeIf(Question::isDeleted);
			quiz.setQuestions(questionList);
		}
		return quizMapper.entitiesToDtos(allQuizzes);
//		return quizMapper.entitiesToDtos(quizRepository.findAllByDeletedFalse());
	}

	@Override
	public QuestionResponseDto getRandomQuestion(Long quizId) {
		Quiz quiz = getQuiz(quizId);
		Random random = new Random();
		Integer randomQuestionIndex = random.nextInt(quiz.getQuestions().size());
		return questionMapper.entityToDto(quiz.getQuestions().get(randomQuestionIndex));
	}

	@Override
	public QuizResponseDto createQuiz(QuizResponseDto quizResponseDto) {
		if (quizResponseDto.getName() == null) {
			throw new BadRequestException("Bad Request. Name is required for creating a quiz");
		}
		if (quizResponseDto.getQuestions() == null) {
			quizResponseDto.setQuestions(new ArrayList<>());
		}
		Quiz quizToSave = quizRepository.saveAndFlush(quizMapper.dtoToEntity(quizResponseDto));
		for (Question question : quizToSave.getQuestions()) {
			question.setQuiz(quizToSave);
			questionRepository.saveAndFlush(question);
			for (Answer answer : question.getAnswers()) {
				answer.setQuestion(question);
			}
			answerRepository.saveAllAndFlush(question.getAnswers());
		}
		return quizMapper.entityToDto(quizToSave);
	}

	@Override
	public QuizResponseDto renameQuiz(Long quizId, String newQuizName) {
		if (newQuizName == null || newQuizName.isBlank()) {
			throw new BadRequestException("Bad request. New name cannot be null or empty");
		}
		Quiz quiz = getQuiz(quizId);
		quiz.setName(newQuizName);
		quizRepository.save(quiz);
		return quizMapper.entityToDto(quiz);
	}

	@Override
	public QuizResponseDto addQuestionToQuiz(Long quizId, Question question) {
		if (question == null || question.getText().isBlank()) {
			throw new BadRequestException("Bad request. Question cannot be null or empty");
		}
		Quiz quiz = getQuiz(quizId);
		question.setQuiz(quiz);
		quiz.getQuestions().add(question);
		for (Answer answer : question.getAnswers()) {
			answer.setQuestion(question);
		}
		questionRepository.save(question);
		answerRepository.saveAll(question.getAnswers());
		quizRepository.save(quiz);
		return quizMapper.entityToDto(quiz);
	}

	@Override
	public QuizResponseDto deleteQuizById(Long quizId) {
		Quiz quiz = getQuiz(quizId);
		for (Question question : quiz.getQuestions()) {
			for (Answer answer : question.getAnswers()) {
				answer.setDeleted(true);
			}
			question.setDeleted(true);
		}
		quiz.setDeleted(true);
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quiz));
	}

	@Override
	public QuestionResponseDto deleteQuestion(Long quizId, Long questionId) {
		Quiz quiz = getQuiz(quizId);
		Optional<Question> optionalQuestion = questionRepository.findById(questionId);
		if (optionalQuestion.isEmpty()) {
			throw new NotFoundException("Question not found");
		}
		for (Question question : quiz.getQuestions()) {
			if (question == optionalQuestion.get()) {
//			if (question == questionRepository.getById(questionId)) {
				for (Answer answer : question.getAnswers()) {
					answer.setDeleted(true);
				}
				question.setDeleted(true);
			}
		}
		questionRepository.save(optionalQuestion.get());
		answerRepository.saveAll(optionalQuestion.get().getAnswers());
		quizRepository.save(quiz);
		return questionMapper.entityToDto(optionalQuestion.get());
	}

}
