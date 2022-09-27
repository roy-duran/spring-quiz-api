package com.cooksys.quiz_api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

	private final QuizService quizService;

	@GetMapping
	public List<QuizResponseDto> getAllQuizzes() {
		return quizService.getAllQuizzes();
	}

	// TODO: Implement the remaining 6 endpoints from the documentation.

//	QuestionResponseDto getRandomQuestion(Long questionId);
	@GetMapping("/{id}/random")
	public QuestionResponseDto getRandomQuestion(@PathVariable Long id) {
		return quizService.getRandomQuestion(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public QuizResponseDto createQuiz(@RequestBody QuizResponseDto quizResponseDto) {
		return quizService.createQuiz(quizResponseDto);
	}

//	QuizResponseDto renameQuiz(Long quizId, String newQuizName);
	@PatchMapping("/{id}/rename/{name}")
	public QuizResponseDto renameQuiz(@PathVariable Long id, @PathVariable String name) {
		return quizService.renameQuiz(id, name);
	}

//	QuizResponseDto addQuestionToQuiz(Long quizId, Question question);
	@PatchMapping("/{id}/add")
	public QuizResponseDto addQuestionToQuiz(@PathVariable Long id, @RequestBody Question question) {
		return quizService.addQuestionToQuiz(id, question);
	}

//	QuizResponseDto deleteQuizById(Long quizId);
	@DeleteMapping("/{id}")
	public QuizResponseDto deleteQuizById(@PathVariable Long id) {
		return quizService.deleteQuizById(id);
	}

//	QuestionResponseDto deleteQuestion(Long questionId);
	@DeleteMapping("/{id}/delete/{questionId}")
	public QuestionResponseDto deleteQuestion(@PathVariable Long id, @PathVariable Long questionId) {
		return quizService.deleteQuestion(id, questionId);
	}

}
