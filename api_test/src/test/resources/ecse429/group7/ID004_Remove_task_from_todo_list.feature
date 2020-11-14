Feature: Remove task from to do list

As a student,
I remove an unnecessary task from my course to do list,
so I can forget about it.

  Background: 
    Given the API server is running
      And the following projects exist on the system
      | title    | completed | active | description            | 
      | ECSE 429 | false     | true   | Software validiation   | 
      | ECSE 444 | true      | false  | Microprocessors        | 
      | ECSE 428 | false     | false  | Software Eng. Practice | 
      | FACC 400 | false     | false  | A do nothing class     | 
      And the following todos are associated with 'ECSE 429'
      | title                           | doneStatus | description              | 
      | Complete feature files          | false      | includes this task       | 
      | Finish project part A           | true       | exploratory & unit tests | 
      | Add unit tests to feature files | false      | Use cucumber             | 
      And the following todos are associated with 'ECSE 444'
      | title               | doneStatus | description       | 
      | Complete labs       | true       | includes lab 1-7  | 
      | Complete quizzes    | true       | includes quiz 1-6 | 
      | Complete final proj | true       | driving game      | 
      And the following todos are associated with 'ECSE 428'
      | title            | doneStatus | description                    | 
      | Finish sprint 2  | false      | Need to finish front end tests | 
      | Write class test | false      | Mid november                   | 
      And no todos are associated with 'FACC 400'
  
  Scenario Outline: Remove valid task from todo list (Normal Flow)
    Given <projectTitle> is the title of a class on the system
      And the class with the title <projectTitle> has outstanding tasks
     When the student requests to delete an existing task with title <todoTitle>
     Then <statusCode> is returned.
    Examples: 
      | projectTitle | todoTitle              | statusCode | 
      | ECSE 429     | Complete feature files | 200       | 
  
  Scenario Outline: Remove all tasks from course (Alternate Flow)
    Given <projectTitle> is the title of a class on the system
      And the class with the title <projectTitle> has outstanding tasks
     When the student requests to delete all tasks from <projectTitle>
     Then the <n> todos from <projectTitle> are removed
      And a <statusCode> is returned
    Examples: 
      | projectTitle | n | statusCode | 
      | ECSE 444     | 3 | 200        | 
  
  Scenario Outline: Remove non-existing task (Error Flow)
    Given <projectTitle> is the title of a class on the system
      And the class with title <projectTitle> has no tasks
     When the student requests to delete an existing task with title <todoTitle>
     Then <statusCode> is returned.
    Examples: 
      | projectTitle | statusCode | 
      | FACC 400     |  404       | 
