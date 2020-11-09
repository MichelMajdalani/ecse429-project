Feature:
As a student, 
I want to change a task description, 
to better represent the work to do.

  Background: 
    Given the API server is running
      And the following todos registered in the system
      | title                  | doneStatus | description                                          | 
      | Finish feature files   | false      | Create 3 feature files (including this one)          | 
      | Complete chem homework | true       | Need to write up a lab and finish challenge question | 
      | 56 (Weird name)!       | false      | This todo exists to try different titles             | 
  
  Scenario Outline: Change description for existing todo (Normal Flow)
    Given <selectedTitle> is the title of a todo registered on the system
     When the user requests to set the description of the todo with title "<selectedTitle>" to "<newDescription>"
     Then the description of the todo with title "<selectedTitle>" will be changed to "<newDescription>"
      And the user will be given the updated version of the todo where the description is <newDescription>
    Examples:
      | selectedTitle          | newDescription                             |
      | Finish feature files   | Create 2 feature files                     |
      | Complete chem homework | Finish quiz                                |
      | 56 (Weird name)!       | This to do exists to test different titles |

#  Scenario Outline: Remove description from existing todo (Alternate Flow)
#    Given <selectedTitle> is the title of a todo registered on the system
#     When the user requests to remove the description of the todo with title <selectedTitle>
#     Then the description of the todo will be removed
#      And the user will be given the update version of the todo with an empty description
#    Examples:
#      | selectedTitle          |
#      | Finish feature files   |
#      | Complete chem homework |
#      | 56 (Weird name)!       |
#
#  Scenario Outline: Change description for non-existent todo (Error Flow)
#    Given <selectedTitle> is not the title of a todo registered on the system
#     When the user requests to change the description of the todo with title <selectedTitle>
#     Then no todo will be updated
#      And the user will be given an error explaining that the todo doesn't exist
#    Examples:
#      | selectedTitle        |
#      | Fake title!          |
#      |                      |
#      | finish feature files |
#
#
