# babystep-timer
A simple application to make TDD practising/work easier.

I've developed the babystep-timer because I love to practise and do Test-Driven-Development.
To learn and practise TDD there are several code katas available on the internet. One basic rule
of TDD is to do so called baby steps.

To learn more about TDD take a look at https://tddmanifesto.com.

## What are Baby Steps in TDD?

1. Setup source control repository.

2. Setup a timer for 2 minutes interval when you start.

3. Write exactly one test

    If the timer rings and the test is red then revert and start over.

    If the test is green before timer rings then commit.

4. Restart timer (no discussions in between timers)

5. Refactor

    If the timer rings and the refactoring is not complete then revert and start over.

    If the refactoring is complete before the timer rings then commit.

6. Restart the timer (no discussions in between timers)

7. Go to 3.

See source for further information: https://blog.adrianbolboaca.ro/2013/03/taking-baby-steps/

## How to use it
The application can be started by executing the babystep-timer.jar file.
When the application runs, a visible timer will be shown in a translucent window.
You can drag the window by clicking and holding on the label beneath the buttons.
The buttons will be fully visible on hovering over them.
The timer runs for 2 minutes. When the timer expires a dialog window will be shown where you can choose...
* to exit the application
* restart the timer
* commit the current changes made in the repository and then restart the timer
* revert the current changes in the repository and then restart the timer

After choosing an option the window disappears and you can continue with your development work until the timer window shows up again.

## Why should I use the babystep-timer?
By using the babystep-timer you are able to focus on the development work while the timer runs and let you choose what to do when the time is up. 

Some examples: 
* if the timer expired and you are still in the red phase of the TDD cycle, you can choose to revert the code changes made and restart the timer
* if the timer expired and you are in the green phase of the TDD cycle following the rules you can choose to commit the code changes made and restart the timer
* if the timer expired and you are still in the refactoring phase of the TDD cycle while having green tests you can choose to just restart the timer

## Contribute
If you want to contribute to this project feel free to fork the repository. I would appreciate any help in developing the babystep-timer further to make it
more user friendly than it is now.

If something is wrong with the timer, please don't hesitate to create an issue or create a pull-request with your code changes.

## MIT License
Copyright (c) 2022 rackberg

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
