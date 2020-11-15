Feature: 
As a student
I create a todolist for a new class I am taking,
so I can manage course work.

  Background: 
    Given the API server is running
  
  Scenario Outline: Create a new course (project) (Normal Flow)
    Given  the course with "<courseTitle>" is not in the system:
     When user requests to create a course with title "<courseTitle>" and description "<courseDescription>"
     Then the course with title "<courseTitle>" and description "<courseDescription>" should be created:
  
    Examples: 
      | courseTitle | completed | active | courseDescription              | 
      | ECSE 429    | false     | true   | Software Validation            | 
      | ECSE 415    | false     | true   | Computer Vision                | 
      | COMP 251    | true      | false  | Data Structures and Algorithms | 
  
  Scenario Outline: Update active status of a project (Alternate Flow)
    Given the course with title "<courseTitle>", active status "<oldActive>" is registered in the system:
     When user requests to set the the active status of the course with title "<courseTitle>" to "<newActive>"
     Then the active status of the course with title "<courseTitle>" should be set to "<newActive>"
  
    Examples: 
      | courseTitle | completed | oldActive | newActive | courseDescription              | 
      | ECSE 429    | false     | true      | false     | Software Validation            | 
      | ECSE 415    | false     | true      | false     | Computer Vision                | 
      | COMP 251    | true      | false     | true      | Data Structures and Algorithms | 
  
  Scenario Outline: Creating a course with a non-existing completed status (Error Flow)
    Given  the course with "<courseTitle>" is not in the system:
     When user requests to create a course with title "<courseTitle>" and completed status "<completed>"
     Then the system should output an error code "<code>"
  
    Examples: 
      | courseTitle | completed    |  courseDescription   | code | 
      | ECSE 429    | ongoing      |  Software Validation | 400  | 
      | ECSE 429    | not finished |  Software Validation | 400  | 
  
