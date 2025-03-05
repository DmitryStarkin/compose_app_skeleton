## Introduction

This module is a framework for creating a simple (or maybe complex) Android Compose application.
The module provides a navigation system and typical actions in the application.<br/>
**the module is currently under testing**

## Architecture

Single Activity<br/>
MVI  с ViewModel

## Usage:

&nbsp;&nbsp;&nbsp; Create an Activity by inheriting it from ```BaseComposeActivity```
Provide her with a ```CommonModel```  instance (must be a ```CommonModelImpl``` or extend it)
With an instance of the 
```Router ``` (```RouterImpl```) and error handler```ErrorHandler```
You can create this directly with 
```CommonModelImpl(ErrorHandler(context),Router())```
or use DI

The Common Model must exist in a single instance within the Activity.
In order to navigate and send events, each screen must have a link to the CommonModelImpl instance.
If the screen has its own ViewModel, then this can usually be implemented like this

```kotlin
class SomeViewModel(
	private val commonViewModel: CommonModel
	) : ViewModel(),  CommonModel by commonViewModel

```
or in any convenient way

If the screen does not have ViewModel (undesirable practice) then the framework
Provides access to the ```localCommonModel```, which will provide an instance 
```CommonModelImpl``` (```localCommonModel.current```)

&nbsp;&nbsp;&nbsp; To implement the screen, create a class inheriting it from

```ComposeScreen``` 
or 
```ComposeDialog```

describe Ui in ```override val content```
These classes have a lifecycle. Lambda ```content``` receives input data as a ```Bundle```

&nbsp;&nbsp;&nbsp; To create navigation, use the ```CommonModel.CreateNavHostHere``` function.

By giving her the controller, the list of screens and the start screen (if necessary)
for example
```kotlin
viewModel.CreateNavHostHere(
	navController, 
	listOf(
		FirstPage::class.java),
		SecondPage::class.java,
		ThirdPage::class.java,
		FourPage::class.java),
		FirstPage::class.java )

``` 
after that, you can navigate using the ```CommonModel.moveToTarget``` functions,
passing ```NavigationTarget``` and input data to it if necessary.

&nbsp;&nbsp;&nbsp; The Common Model also provides flows :

```externalEventFlow``` - data from outside the application

put this data in the ```MainActivity``` using the ```CommonModel.onExternalEvent``` function by putting ```KeyedData``` there

Subscribers will receive the data and can extract it using the key, if they know it.
```KeyedData.requestData(key)```

```navigationEventFlow``` – the current navigation position and the data that is returned when you go back (put them when you go to ```NavigationTarget.MoveBack```)

### See the testApp for more details.