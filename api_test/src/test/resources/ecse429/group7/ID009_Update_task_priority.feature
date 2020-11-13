Feature: Update task priority

As a student,
I want to adjust the priority of a task,
to help better manage my time.

  Background: 
    Given the API server is running
      And the following priorities are registered in the system:
      | title  | description          | 
      | HIGH   | High Priority Task   | 
      | MEDIUM | Medium Priority Task | 
      | LOW    | Low Priority Task    | 
  
  Scenario Outline: Update Task priority from "<todoPriorityTask>" priority to "<todoUpdatedPriorityTask>" priority (Normal Flow)
    Given the todo with name "<todoTitle>", status "<todoDoneStatus>", description "<todoDescription>" is registered in the system
      And the todo with title "<todoTitle>" is assigned as a "<todoPriorityTask>"
     When user requests to update the priority categorization of the todo with title "<todoTitle>" from "<todoPriorityTask>" to "<todoUpdatedPriorityTask>"
     Then the todo with title "<todoTitle>" should be classified as a "<todoUpdatedPriorityTask>" priority task
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription     | todoPriorityTask | todoUpdatedPriorityTask | 
      | ECSE 429 Project A | false          | ECSE 429 Unit Tests | HIGH             | LOW                     | 
      | Sprint             | true           | ECSE 428 Tests      | HIGH             | MEDIUM                  | 
      | B                  | true           | ECSE 429 Project    | MEDIUM           | HIGH                    | 
      | Lab0               | false          | ECSE 420 Lab        | MEDIUM           | LOW                     | 
      | Assignment3        | true           | ECSE 415 Assignment | LOW              | MEDIUM                  | 
      | Final              | false          | Final               | LOW              | HIGH                    | 
  
  Scenario Outline: Update Task priority to the same priority (Alternate Flow)
    Given the todo with name "<todoTitle>", status "<todoDoneStatus>", description "<todoDescription>" is registered in the system
      And the todo with title "<todoTitle>" is assigned as a "<todoPriorityTask>"
     When user requests to update the priority categorization of the todo with title "<todoTitle>" from "<todoPriorityTask>" to "<todoPriorityTask>"
     Then the todo with title "<todoTitle>" should be classified as a "<todoPriorityTask>" priority task
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription     | todoPriorityTask | 
      | ECSE 429 Project A | false          | ECSE 429 Unit Tests | HIGH             | 
      | B                  | true           | ECSE 429 Project    | MEDIUM           | 
      | Final              | false          | Final               | LOW              | 
  
  Scenario Outline: Update Task priority to the multiple priorities (Error Flow)
    Given the todo with name "<todoTitle>", status "<todoDoneStatus>", description "<todoDescription>" is registered in the system
      And the todo with title "<todoTitle>" is assigned as a "<todoPriorityTask>"
     When user requests to add a priority categorization of "<todoNewPriorityTask>" to the todo with title "<todoTitle>" with "<todoPriorityTask>"
     Then an error code "<errorCode>" should be returned
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription     | todoPriorityTask | todoNewPriorityTask | errorCode | 
      | ECSE 429 Project A | false          | ECSE 429 Unit Tests | HIGH             | MEDIUM              | 400       | 
      | Sprint             | true           | ECSE 428 Tests      | LOW              | MEDIUM              | 400       | 
      | B                  | true           | ECSE 429 Project    | MEDIUM           | HIGH                | 400       | 
  
  Scenario Outline: Update task to non-existing category (Error Flow)
    Given the todo with name "<todoTitle>", status "<todoDoneStatus>", description "<todoDescription>" is registered in the system
      And the todo with title "<todoTitle>" is assigned as a "<todoPriorityTask>"
     When user requests to update the priority categorization of the todo with title "<todoTitle>" from "<todoPriorityTask>" to "<invalidTodoPriority>"
     Then an error code "<errorCode>" should be returned
  
    Examples: 
      | todoTitle          | todoDoneStatus | todoDescription     | todoPriorityTask | invalidTodoPriority | errorCode | 
      | ECSE 429 Project A | false          | ECSE 429 Unit Tests | HIGH             | BAHAMAS             | 400       | 
      | Sprint             | true           | ECSE 428 Tests      | LOW              | BAMBOO              | 400       | 
      | B                  | true           | ECSE 429 Project    | MEDIUM           | YESTERDAY           | 400       | 
