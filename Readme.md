## Introduction

This module is a framework for creating a simple (or maybe complex) Android Compose application.
The module provides a navigation system  (as an add-on to the standard navigation) and typical actions in the application.<br/>
**the module is currently under testing**

## Architecture

Single Activity<br/>
MVI  с ViewModel

## Usage:

&nbsp;&nbsp;&nbsp; Create an Activity by inheriting it from ```BaseComposeActivity```
Provide her with a ```AppLevelActionController```  instance (must be a ```AppLevelActionControllerImpl``` or extend it)
With an instance of the 
```Router ``` (```RouterImpl```) and error handler```ErrorHandler```
You can create this directly with 
```AppLevelActionControllerImpl(ErrorHandler(context),Router())```
or use DI

The Common Model must exist in a single instance within the Activity.
In order to navigate and send events, each screen must have a link to the CommonModelImpl instance.
If the screen has its own ViewModel, then this can usually be implemented like this

```kotlin
class SomeViewModel(
	private val appLevelActionController: AppLevelActionControllerImpl
	) : ViewModel(),  AppLevelActionController by appLevelActionController

```
or in any convenient way

If the screen does not have ViewModel (undesirable practice) then the framework
Provides access to the ```localAppLevelActionController```, which will provide an instance 
```AppLevelActionController``` (```localAppLevelActionController.current```)

&nbsp;&nbsp;&nbsp; To implement the screen, create a class inheriting it from

```ComposeScreen``` 
or 
```ComposeDialog```
you can override  the onCreate etc.

also if you want to make your class as ```LifeCycleOwner``` , you can extend the ```ComposeScreenWithLifeCycle()``` or ```ComposeDialogWithLifeCycle()``` instead.
These classes have a ```lifecycle``` field and ability to add observers

describe Ui in ```override val content```
 Lambda ```content``` receives input data as a ```Bundle```

&nbsp;&nbsp;&nbsp; To create navigation, use the ```AppLevelActionController.CreateNavHostHere``` function.

By giving her the list of screens and the start screen (if necessary)
for example
```kotlin
viewModel.CreateNavHostHere(
	targets = listOf(
		FirstPage::class.java,
		SecondPage::class.java,
		ThirdPage::class.java,
		FourPage::class.java),
	startTarget = FirstPage::class.java )

``` 
after that, you can navigate using the ```AppLevelActionController.moveToTarget``` functions,
passing ```NavigationTarget``` and input data to it if necessary.

&nbsp;&nbsp;&nbsp; The AppLevelActionController also provides flows :

```externalEventFlow``` - data from outside the application

put this data in the ```MainActivity``` using the ```AppLevelActionController.onExternalEvent``` function by putting ```KeyedData``` there

Subscribers will receive the data and can extract it using the key, if they know it.
```KeyedData.requestData(key)```

```navigationEventFlow``` – the current navigation position and the data that is returned when you go back (put them when you go to ```NavigationTarget.MoveBack```)

### See the testApp for more details.

## Installation:

1 in project level build.gradle add:
```

allprojects {
repositories {
........
        maven { url "https://jitpack.io" }
        }
   }
```
or in setting.gradle add:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
    ......
    maven { url 'https://jitpack.io' }
    }
}
```

2 in module level build.gradle add:
```
dependencies {
...........
         implementation 'com.github.DmitryStarkin:compose_app_skeleton::version'
   }
```
