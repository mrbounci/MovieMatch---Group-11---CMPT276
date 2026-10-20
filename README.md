# MovieMatch---Group-11---CMPT276
MovieMatch – Project Proposal Project Overview 


MovieMatch is a browser based application designed to help groups of friends decide what movie to watch together. Choosing a movie can be difficult when multiple people have different preferences, often resulting in long discussions without reaching an agreement. Our application aims to solve this problem through a collaborative voting system, movie recommendations, rankings, and user reviews.

The primary target audience is groups of friends and movie enthusiasts who regularly watch movies together. MovieMatch is mainly an entertainment focused application that aims to make group movie selection faster, easier, and more enjoyable.

 
Existing Solutions and Improvements 

Currently, people often decide what to watch by discussing their preferences, browsing streaming services, or checking movie reviews and ratings online. Platforms such as IMDb and Letterboxd allow users to discover movies, read reviews, and view ratings. However, these platforms primarily focus on individual movie discovery and reviewing rather than collaborative movie selection.
Other applications, such as Cinelect, already provide group-based movie selection and voting. However, MovieMatch aims to combine collaborative voting with user-written reviews, personalized recommendations, and community-based movie rankings in one platform. This will allow groups to discover potential movies through community feedback before deciding what to watch.
MovieMatch will combine these features with a group voting system. Users will be able to nominate movies, vote on their preferred choices, and use recommendations or rankings when they do not already have movies in mind. This will provide a more convenient way for groups to reach a decision.


Project Scope and Features 

Our application will consist of five main epics, providing enough work to divide responsibilities among five group members. 
User Authentication and Account Management: Users will be able to sign up, log in, manage their accounts, and reset their passwords using their registered email addresses in their profile. They will also be able to friend other users and customize their profile.
Group Movie Voting: Users will be able to create movie voting sessions, invite friends, nominate movies, and vote to determine the group’s preferred choice. 
Movie Recommendations: Users will receive movie suggestions based on genres, popularity, and ratings, helping them discover movies to add to voting sessions. 
Movie Reviews: Registered users will be able to rate movies and write reviews to share their opinions with others. Users will also be able to comment on movie reviews and report inappropriate/spam reviews.
Movie Rankings: Movies will be ranked based on user ratings and feedback. Groups can browse these rankings to discover popular movies and add them to their voting sessions. 

API and Technology 

We plan to use The Movie Database (TMDB) REST API to retrieve information such as movie titles, genres, posters, descriptions, and ratings. Our application will be developed using Spring Boot for the backend and HTML, CSS, and JavaScript for the frontend. We will use a database to store accounts, votes, and reviews. The application will be hosted on Render, with the source code maintained in a public GitHub repository. TMDB API: https://developer.themoviedb.org/docs/getting-started

 First Group Meeting 

During our initial group discussion, we decided to create a movie review and voting application focused on helping groups choose movies together. We identified the main features, discussed the possible use of the TMDB API, and considered how the project could be divided into five epics. We also discussed each member's programming experience and familiarity with web development. We agreed to divide the project into five major features based on our individual strengths and interests. We have created a shared GitHub repository and agreed to use Discord for communication. Our next steps include researching the TMDB API and setting up the initial Spring Boot application.

