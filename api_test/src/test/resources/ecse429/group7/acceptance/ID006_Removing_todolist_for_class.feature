Feature: 
As a student
I remove a todolist for a class which I am no longer taking,
So I can declutter my schedule.

  Background: 
    Given the API server is running
  
  Scenario Outline: Remove an existing course (Normal Flow)
    Given the course with title "<courseTitle>" is registered in the system:
  
     When user requests to delete the course with title "<courseTitle>"
     Then the course with title "<courseTitle>" should be removed from the system 
  
    Examples: 
      | courseTitle | completed | active | description         | 
      | ECSE 429    | false     | false  | Software validation | 
      | ECSE 415    | false     | true   | Computer Vision     | 
  
  Scenario Outline: Delete tasks associated with course (Alternate Flow)
    Given the course with title "<courseTitle>" is registered in the system:
      And that the todos with title "<todoTitle>" being a task of "<courseTitle>"
  
     When user requests to delete todos task of "<courseTitle>"
     Then the todos task of "<courseTitle>" should be removed
    Examples: 
      | todoTitle    | doneStatus | description           | courseTitle | 
      | Project A    | true       | Complete unit testing | ECSE 429    | 
      | Project B    | false      | Gherkin Files         | ECSE 429    | 
      | Assignment 2 | true       | Recognize Faces       | ECSE 415    | 
  
  Scenario Outline: Deleting a course that doesn't exist (Error Flow)
    Given the course with title "<courseTitle>" is registered in the system:
     When user requests to delete a course with title "<invalidTitle>"
     Then the system should output an error
  
    Examples: 
      | invalidTitle | completed | active | description         | 
      | ECSE 429     | true      | true   | Software validation | 
      | ECSE 415     | false     | true   | Computer Vision     | 
  
  
