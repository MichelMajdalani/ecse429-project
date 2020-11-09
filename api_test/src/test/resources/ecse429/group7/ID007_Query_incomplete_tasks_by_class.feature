Feature:
As a student,
I query the incomplete tasks for a class I am taking,
to help manage my time.

  Background:
    Given the following projects exist on the system
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
#
#  Scenario Outline: Request incomplete tasks for a course incomplete tasks (Normal flow)
#    Given <projectTitle> is the title of a class on the system
#      And the class with title <projectTitle> has outstanding tasks
#     When the user requests the incomplete tasks for the course with title <projectTitle>
#     Then <n> todos will be returned
#      And each todo returned will be marked as done
#      And each todo returned will be a task of the class with title <projectTitle>
#    Examples:
#      | projectTitle | n |
#      | ECSE 429     | 2 |
#      | ECSE 428     | 2 |
#
#  Scenario Outline: Request incomplete tasks for a course with no incomplete tasks (Alternate flow)
#    Given <projectTitle> is the title of a class on the system
#      And the class with title <projectTitle> has no outstanding tasks
#     When the user requests the incomplete tasks for the course with title <projectTitle>
#     Then 0 todos will be returned
#    Examples:
#      | projectTitle |
#      | ECSE 444     |
#
#  Scenario Outline: Request incomplete tasks for a course with no tasks (Alternate flow)
#    Given <projectTitle> is the title of a class on the system
#      And the class with title <projectTitle> has no tasks
#     When the user requests the incomplete tasks for the course with title <projectTitle>
#     Then 0 todos will be returned
#    Examples:
#      | projectTitle |
#      | FACC 400     |
#
#  Scenario Outline: Request incomplete tasks for a course not registered on the system (Error flow)
#    Given <projectTitle> is not a title of a class on the system
#     When the user requests the incomplete tasks for the course with title <projectTitle>
#     Then 0 todos will be returned
#      And the user will receive an error telling them that the task doesn't exist on the system
#    Examples:
#      | projectTitle |
#      | FACC 100      |
#      | Doesn't exist |
#
#
  Scenario: Dummy
    Given the API server is running
