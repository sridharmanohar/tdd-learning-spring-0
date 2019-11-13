# User Story
As a user, I want to see a list of all metropolitan cities that exists today and also to be able to propose a new city for being promoted to a metropolitan city.
    
# Acceptance Test - 1
Show all current metro cities.
## Task
a0. Maintain a static map of all existing metros along with a status 'confirmed' against each city.
### TestCases
a0.0. veifyInitialMapSizeGreaterThanZero
a0.1. verifyMapHasNoNullStatus

## Task
a1. Display the contents of the static map when user hits "/metros".
### TestCases
a1.0. whenMetros__thenDisplayMapContents

# Acceptance Test - 2
Add a new city.
## Tasks
b0. Add the user provided city align with a status 'proposed' in the static map.
## TestCases
b0.0. whenDuplicateMetro__thenDuplicateKeyException
b0.1. whenNewMetro__thenVerifyMetrowithStatusProposed
 
# Acceptance Test - 3
Now, show all metros along with the proposed ones with 'proposed' as a status against them.
## Tasks
c0. Display the contents of the static map when user hits "/metros".
## TestCases
Covered above.