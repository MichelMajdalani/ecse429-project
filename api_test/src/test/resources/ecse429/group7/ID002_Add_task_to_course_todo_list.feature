Feature: 
As a student
I add a task to a course todo list,
so I can remember　it.

  Background: 
    Given the API server is running
  
  Scenario Outline: Add task to a specific project's todo list  (Normal Flow)
    Given the todo with name "<todoTitle>" and description "<todoDescription>" is registered in the system:
     When the user requests to add the todo with name "<todoTitle>" and description "<todoDescription>" to the course with title "<courseTitle>"
     Then the todo with name "<todoTitle>" should be added to the todo list of the course with title "<courseTitle>"
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription       | courseTitle | 
      | ECSE 429 Project B | false          | ECSE 429 User Stories | ECSE 429    | 
      | ECSE 415 Project 3 | false          | ECSE 415 CNNs         | ECSE 415    | 
      | ECSE 420 Lab 2     | true           | Shared Memory         | ECSE 420    | 
  
  Scenario Outline: Add a course and a task simultaneously (Alternate Flow)
    Given the course with "<courseTitle>" is not in the system:
     When the user requests to add the course with "<courseTitle>" to the system:
     And the user requests to add the todo with name "<todoTitle>" and description "<todoDescription>" to the course "<courseTitle>"
     Then the todo with name "<todoTitle>" should be added to the todo list of the course with title "<courseTitle>"
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription       | courseTitle | 
      | ECSE 429 Project B | false          | ECSE 429 User Stories | ECSE 429    | 
      | ECSE 415 Project 3 | false          | ECSE 415 CNNs         | ECSE 415    | 
      | ECSE 420 Lab 2     | true           | Shared Memory         | ECSE 420    | 
  
  Scenario Outline: Add task specifying a project that doesn't exist (Error Flow)
    Given the todo with name "<todoTitle>" is registered in the system:
      And the only course in the database is the course with title "<courseTitle>"
     When user requests to add the todo with name "<todoTitle>" to the project title "<inavlidCoursetTitle>"
     Then the system should output an error code "<errorCode>"
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription       | invalidCourseTitle | errorCode | courseTitle |
      | ECSE 429 Project B | false          | ECSE 429 User Stories | ECSE 428           | 404       | ECSE 429    |
      | Assignment 4       | False          |                       | ECSE 415           | 404       | ECSE 429    |
  
  
