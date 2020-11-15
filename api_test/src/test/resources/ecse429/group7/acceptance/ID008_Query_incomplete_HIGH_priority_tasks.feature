Feature: Query incomplete HIGH priority tasks

As a student,
I query all incomplete HIGH priority tasks from all my classes,
to identity my short-term goals.

  Background: 
    Given the API server is running
      And the following priorities are registered in the system:
      | title  | description          | 
      | HIGH   | High Priority Task   | 
      | MEDIUM | Medium Priority Task | 
      | LOW    | Low Priority Task    | 
      And the following projects exist on the system
      | title    | completed | active | description            | 
      | ECSE 429 | false     | true   | Software validiation   | 
      | ECSE 444 | true      | false  | Microprocessors        | 
      | ECSE 428 | false     | false  | Software Eng. Practice | 
      | FACC 400 | false     | false  | A do nothing class     | 
      And the following todos are associated with 'ECSE 429'
      | todoTitle      | todoDoneStatus | todoDescription                | todoPriority | 
      | Assignment 2   | true           | Complete unit testing          | HIGH         | 
      | Midterm Review | false          | Review session offered by CSUS | HIGH         | 
      | Lab            | false          | Review session offered by EUS  | MEDIUM       | 
      And the following todos are associated with 'ECSE 444'
      | todoTitle    | todoDoneStatus | todoDescription   | todoPriority | 
      | Assignment 1 | true           | Complete ML model | LOW          | 
      And the following todos are associated with 'ECSE 428'
      | todoTitle | todoDoneStatus | todoDescription             | todoPriority | 
      | Paper     | false          | Interview Software Engineer | HIGH         | 
      And no todos are associated with 'FACC 400'
  
  Scenario Outline: Query incomplete HIGH priority tasks from a course with incomplete HIGH priority tasks (Normal Flow)
  
    Given <projectTitle> is the title of a class on the system
      And the class with title <projectTitle> has outstanding tasks
     When the user requests the incomplete HIGH priority tasks for the course with title <projectTitle>
     Then <n> todos will be returned
      And each todo returned will be marked as done
      And each todo returned will have a HIGH priority
      And each todo returned will be a task of the class with title <projectTitle>
    Examples: 
      | projectTitle | n | 
      | ECSE 429     | 1 | 
      | ECSE 428     | 1 | 
  
  Scenario Outline: Query incomplete HIGH priority tasks from a course with no incomplete HIGH priority tasks (Alternate flow)
    Given <projectTitle> is the title of a class on the system
      And the class with title <projectTitle> has no outstanding tasks
     When the user requests the incomplete HIGH priority tasks for the course with title <projectTitle>
     Then 0 todos will be returned
    Examples: 
      | projectTitle | 
      | ECSE 444     | 
  
  Scenario Outline: Query incomplete HIGH priority tasks from a course with no tasks (Alternate Flow)
    Given <projectTitle> is the title of a class on the system
      And the class with title <projectTitle> has no tasks
     When the user requests the incomplete HIGH priority tasks for the course with title <projectTitle>
     Then 0 todos will be returned
    Examples: 
      | projectTitle | 
      | FACC 400     | 
  
  Scenario Outline: Query incomplete HIGH priority tasks from invalid course (Error Flow)
    Given <projectTitle> is not a title of a class on the system
     When the user requests the incomplete HIGH priority tasks for the course with title <projectTitle>
     Then 0 todos will be returned
      And the user will receive an error telling them that the course doesn't exist on the system
    Examples: 
      | projectTitle  | 
      | FACC 100      | 
      | Doesn't exist | 
