Feature: 
As a student
I categorize tasks as HIGH, MEDIUM or LOW priority
So I can better manage my time

  Background: 
    Given the following categories are registered in the todoManagerRestAPI system:
      | title  | description          | 
      | HIGH   | High Priority Task   | 
      | MEDIUM | Medium Priority Task | 
      | LOW    | Low Priority Task    | 
  
  Scenario: Categorize Task as HIGH Priority (Normal Flow)
    Given the following todo is registered in the system:
      | title              | doneStatus | description           | 
      | ECSE 429 Project B | false      | ECSE 429 User Stories | 
     When user requests to categorize todo with title "ECSE 429 Project B" as "HIGH" priority
     Then the "ECSE 429 Project B" should be classified as a "HIGH" priority task
  
  Scenario: Change Task priority from HIGH priority to LOW priority (Alternate Flow)
    Given the following todo is registered in the system:
      | title              | doneStatus | description         | 
      | ECSE 429 Project A | false      | ECSE 429 Unit Tests | 
      And the todo "ECSE 429 Project A" is assigned as a "HIGH" priority task
     When user requests to remove "HIGH" priority categorization from "ECSE 429 Project A"
      And user requests to add "MEDIUM" priority categorization to "ECSE 429 Project A"
     Then the "ECSE 429 Project A" should be classified as a "MEDIUM" priority task
  
  Scenario: Categorize non-existing task as HIGH priority (Error Flow)
    Given the following todo is registered in the system:
      | title                 | doneStatus | description                  | 
      | ECSE 415 Assignment 3 | false      | Convolutional Neural Network | 
     When user requests to add "HIGH" priority categorization to "ECSE 415 Assignment 21"
     Then the system should output an error message
  
  
