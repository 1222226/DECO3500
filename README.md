### DECO3801/7381 Design Computing Studio 3 - Build Semester 2, 2024 
## Project: Hight Perception
## Team Members: 

1. [Introduction]
   
2. [Installation]
   This project is build on Android Studio. You can download the newest version of Android Stduio, install JDK and Android SDK(Recommend API 34) and open the project to view it. To run it, you can connect an android phone
   (Sumsung etc.), make sure they where under developer mode, and run the project, the app will be install on your phone. Or you can setup virtual devices in Android Studio.
   For this project can interact between users, you'd better use 2 devices.
   This program is using internet so make sure your wifi of data is on. If you want to check and manupilate the backend for yourself, you can setup a Firebase project. To do it, First go to firebase and create an
   account, then create a project, then add a new android application, use 'com.example.test2' as the package name, then follow theinstruction, download the google-services.json file it generates, replace the
   google-services.json file existing(make sure the file name as 'google-services.json' unchanged), then follow the rest instructions. After setting up, you can
   Sync the Project again and run. To make sure the data would upload into backend, you should create the space of Authentication, ReaL-time Database and Storage in Firebase.  
4. [Usage]
   First, you should login. If you are testing on our firebase, the user registed are User1: name:'amy@qq.com', password:'123456';User2: name:'bob@qq.com', password:'123456'. Login both of the user in your devices.
   If you create your firebase, you can setup your users, but make sure there are 2 users login. 
   After that. You can choose a kitchen countertop you want to work on. When you click 'Public Kitchen', it would show a image of kitchen and the Empty spaces. You can choose one and click, going into a page where as a dropbox frame
   and a list of ingredients. You can drag an ingredient into the dropbox for userA, and another ingredient for userB, image of 2 ingredients would appear in both devices. IF you cannot see same ingredients in userA and userB,
   Go back to the previous level and then re-enter. This is also a common method to resolve content synchronization issues between userA and userB interfaces. Then you can click on the dropbox, it would pop a dialog which shows
   the grouping information. You can agree or cancel grouping, if you click agree, it would turn to a page to provide a list of recommend recipe(A recipe menu that includes ingredient A and ingredient B). If you cannot see a recipe
   or it make notification that there is no matched recipes, that means there is no recipe contains the combanation you choose. Some different recipe combinations will have corresponding recipes, but a more reliable approach is to
   ensure that user A and user B put the same ingredients. Then you can click on a receipe and view the detail of it, then click agree means you choose the recipe. If userA and userB choose the different recipe, the 2 receipe would
   appear in green background to inform users. Only if 2 users select the same receipe, that receipe would shows red and the cooking activity is registed. You can view the bookig information by click booking button in the homepage,
   in which you can click the Meal button to review the receipe detail again, click Start Timer to start a timer which is a simulation of a cooking process guide and cancel booking.
   If you choose My Kitchen in homepage, which you want means to invite others to your private kitchen, then your userB should click Private Kitchen where inside is a Scanner. Just scan a whichever can userB enter userA's Kitchen,
   then the two of them can follow the steps above.
   There's a another forum function which you can see by clicking the Community button in the homepage. You can view others post, or submit you own post by input UserID, titile, Content and select a photo from your album.


   
6. [Features]
·Seamless Transition Height Change: The prototype allows players to change their size by interacting with game props like potions. Height transitions are smooth, preventing motion sickness and ensuring a natural experience.
·Cartoon-style Virtual Scene: We built a cartoon-style dungeon scene where users can explore different visual effects and pass through the game levels we designed through height changes.
·Advanced Interactive Features: Key interactive objects like treasure chests, doors, and gears use animations to enhance the gameplay experience. Players can interact with these objects, which respond based on their size and actions.
·Advanced Interactive Features:Level Interaction and Traps: Various traps, such as rotating gears and swinging swords, challenge players to strategically adjust their size to progress. Collecting keys and unlocking doors adds complexity to the level design.
·Jump and Haptic Feedback: The jump mechanic varies according to the player's size, and planned haptic feedback will provide physical sensations during object interactions.
·Manual Interaction and Puzzle Mechanics: Players can manually grab and manipulate objects in the VR environment. Puzzle pieces can be placed on a board, with real-time feedback enhancing interactivity.
7. [DirectoryStructure]
    Here’s an overview of the project’s directory structure:
    projectname/
    │ 3
    ├── Asserts/
    │   ├── Scripts
             ├── ScaleTransform.cs
             ├── OVRPlayerControllerCustomize.cs
             ├── GameFlow.cs
             ├── Ball.cs
             ├── CameraFollow.cs
             ├── CollectPieces.cs
             ├── BackPack.cs
           
    │   └── Prefabs
    │   └── Materials
    │   └── Scenes
            │   └── MyScene
            │   └── MyScene2
    │   └── Source imported
    │   └── ...
   
    │
    ├── packges
    │   ├── ...
    │   └── ...
    └── README.md         # xxxx

8. [License]
The assets used in this project are subject to the Unity Asset Store End User License Agreement (EULA). Please refer to the [Unity Asset Store Terms](https://unity3d.com/legal/as_terms) for more details.


9. [Contact] 
    For any questions or support, please contact:
    - Yucheng.Pan@Student.uq.edu.au
    - [GitHub Profile](https://github.com/1222226/DECO3500)
