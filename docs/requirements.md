# EECS 4443: Mobile User Interfaces - Project (Winter 2015)

## Background
The Project for this course involves designing and building a mobile user
interface and doing a comparative evaluation of the interface. Your first goal
is to prepare and submit a Proposal. The purpose of the proposal is just to get
started – to pick a topic for the project, to investigate and review the topic,
and to propose how the project will be carried out. The platform is Android. The
topic is up to you.

## The Topic
The topic should be something that interests you.  Are you a regular user of a
mobile device?  Probably, yes.  What app do you use most?  What app do you enjoy
most?  Are you an avid gamer?  Do you send 50+ text messages a day?  Are you
hooked on social networking?  Is there an app or feature that you wish you had,
but don't?  Is there an app that frustrates you because it doesn't do what you
want?  These are good starting points for choosing a topic for your 4443
project.

In picking a topic, think ahead to the evaluation.  The evaluation is
comparative because the interface will be compared with some other interface.
The other in "other interface" can be a commercial app with similar features.
More likely, it will be a variation you build in to the UI.  It can be – in
fact, should be – more than one variation.  So, what sort of topic is
appropriate?  Let's continue.

Consider the calendar app found on typical mobile devices.  Are you someone who
meticulously enters, maintains, and follows the schedule of commitments in your
mobile device's calendar?  If so, perhaps building a calendar UI is a good
topic.  But, remember, the project requires a comparative evaluation.  You need
to build two variations of the calendar UI and compare one with the other.
Sounds onerous!  Don't worry, this isn't necessary.  Read on.

A calendar app – to be useful and commercially viable – includes a lot of
features.  There is no need to build and test a full-blown mobile application,
such as a calendar app.  Here's some advice in picking a topic: Think small!
Choose one aspect of the UI and focus on that.  For a calendar app, you could
focus on the module used to create an entry in the calendar.  That's more
realistic.  So, instead of building and evaluating a calendar app, you build and
evaluate a "create entry" module for a calendar app.  The rest of the app
needn't exist.  Or, if it does, a simple mock-up is fine.

OK, but what is it about a create-entry module for a calendar app that makes for
a suitable project topic?  Let's be more specific.

Again, a good tip is to think small.  What properties of interaction are
relevant to the act of creating a calendar entry?  Are there different ways to
arrange the input widgets?  If screen real estate is limited, is there a need
for scrolling, panning, or zooming?  What is the effect on performance?  Is
there a better way?  What about the font (family, size, style, colour)?  What
about navigation?  Does the task require text entry?  Can pickers or spinners be
used in lieu of text entry?  Do they help?  Do they hinder?  Does text entry use
a soft keyboard?  Which keyboard?  Can Graffiti gestures be used?  Are they
better?  Do users like them?  Are they hard to learn?  Can graphics be added to
improve the interaction or experience?  What about touch, swipe, or pinch
gestures?  Can they help in some way to improve the UI?  Does the finger obscure
input?  How is this avoided?  What about device inputs?  The microphone.  Is
there a beneficial way to include speech or non-speech audio input?  The camera.
Hmm, not sure about this, but perhaps you've got an idea.  And there are sensors
for movement, proximity, location, gravity, barometric pressure (!), ...  Can
any of these assist in improving either the user experience or the user's
ability to perform a task?  What about device outputs?  Sound, vibration, ...
Can audio or tactile feedback be added (or removed!) to improve the interaction?
What about visual feedback?  Can it be improved?  What about colour, animation,
transparency, 3D (!), or other visual properties?  At this juncture, you might
consider revisiting the Android [Design Principles](http://developer.android.com/design/get-started/principles.html)
that were reviewed for Assignment 1.  Surely, within the 17 principles there
are opportunities.

The paragraph above nips away at a long list of potential "points of comparison"
– things that can be designed-in, manipulated, and investigated as possible
factors influencing the user's performance or experience with a mobile UI.  Once
you've decided on the topic in a broad sense, narrow it down, then narrow it
down more.  Get right down to the minutiae – small details in the interaction or
the interface that might improve a user's performance or experience (read the
above paragraph again).  There's your topic.

Bear in mind that this is your topic, not mine.  Try to be original.  Please, do
not propose a create-entry module for a calendar app as your project topic.

Early on, when narrowing-in on a topic, do a Google Scholar search to find
research publications on related work.  This is also a good exercise to help you
decide on a topic.  Within any research lies opportunities for new research – a
little tweak here, a little change there.  Your proposal and report require a
literature review (see below), so the earlier you start on this, the better.

By the way, the points of comparison needn't be a different UI.  You can compare
different aspects of the interaction, such a one-handed vs. two-handed, or
sitting vs. standing vs. walking, or outside (wearing gloves ) vs. inside.  Of
course, the points of comparison are the topic.  Relevance to mobile interaction
must be noted with related work reviewed.

Once you've decided on a topic, it's time to develop and test the software.
Develop all the software in a single Android package.  The package name must end
with `projectlogin`, where login is your Prism login ID.

It's important to think about the evaluation at all stages, since the software
must collect and log data

## The Evaluation
For the evaluation, engage at least eight users ("participants") to do tasks
with your UI while their performance is measured and logged.  The task is
critical.  It must be simple and well defined.  There are two properties of a
good task for a comparative evaluation.  A good task must represent and
discriminate.  The task must be representative of the sorts of things users
typically do.  At the same time, the task must have the ability to reveal
differences in the points of comparison.

Your software must measure and log the performance of users as they do the
tasks.  Each iteration of the task is a trial.  If the task is simple and takes
just a few seconds, have the user do the task several times.  You can analyse
the improvement over the trial iterations.  Or, you can analyse the means
(averages).  These possibilities will be elaborated during classroom lectures.

For this project, you are required to log and analyse at least two measures of
user performance.  The most common measures relate to speed and accuracy.  Speed
is usually represented in its reciprocal form – the time to do the task.
Accuracy can be a simple record of whether or not the task was completed
successfully – the number of errors or the error rate.  Or, accuracy can reflect
some other aspect of interaction, such as the number of corrective actions or
the extent of deviation from perfect performance.  Consult Lab 4 and Lab 6 for
details (including Android code!) on measuring and logging user performance
data.

It is also important to "measure" the user's experience.  This is usually done
after testing in the form of a questionnaire or interview, or both.

After your evaluation is complete, the data are examined and analysed, with
results presented and discussed in the report.  The points of comparison are the
test conditions.  Which test condition performed best?  Was it better for all
the measurements, or just some?  How much better?  Why was it better?  Were the
differences statistically significant?  What is the best way to present the
results?  In a table?  In a chart?  A line chart or a bar chart?  Did
participants like the UIs?  Did they like one better than the other?  Why?  Did
the participants have useful comments or suggestions for improvements?

Although the word "experiment" was not used above, the comparative evaluation
has all the ingredients of a typical experiment, often called a user study.  The
core details are in chapter 5 ("Designing HCI Experiments") in this course's
[Suggested Readings](http://www.eecs.yorku.ca/course_archive/2014-15/W/4443/index.html#suggestedreadings).
See also section 6.1 in chapter 6 ("Hypothesis Testing"). We'll also use
classroom lectures to teach the mechanics of doing a user study.

## Optional
Prepare a short video (two minutes or less) demonstrating and explaining your UI
or interaction technique.  Some examples of videos will be shown during
classroom lectures.  Submit the video along with your final report and Android
project source files.  (see below)

## The Report

The complete details on preparing and organizing the report are given in chapter
8 ("Writing and Publishing a Research Paper") in the
[Suggested Readings](http://www.eecs.yorku.ca/course_archive/2014-15/W/4443/index.html#suggestedreadings).

The first section of the report is the **Abstract**.  The abstract is a summary of
the report.  The goal of the astract it to tell the reader "what you did" and
"what you found".  Write the abstract last.  The abstract is short, about 150
words.

The first large section of the report is the **Introduction**.  Begin with high-
level observations about mobile computing and mobile user interfaces.  Then,
introduce the topic.  For the create-entry example, the module is for a calendar
app.  The calendar app provides context for the topic.  Make high-level comments
about the importance and relevance of the topic to mobile computing.  Present
and discuss different examples of current practice.  Use images or screen snaps
to describe and distinguish them.  Describe the topic broadly speaking (e.g.,
calendar apps), then focus on the narrow issue you are investigating (e.g.,
create entry).  Important interaction issues are presented and compared (see
paragraph above, beginning with "Again, a good tip...").  Related work is
reviewed.  Then, you get to your idea – an improved "create entry" module for a
calendar app.  Describe it in detail.  Use figures and other visual aides to
help guide the discussion.

The point that "related work is reviewed" refers to a __literature review__.  Search
using Google Scholar to find publications that investigated the topic, or
similar topics.  Summarize what was done and the results obtained.  Cite and
reference!  Include at least six (6) publications.

After the Introduction comes the **Method** (what you did and how you did it),
then **Results and Discussion**, then **Conclusions**, then **Acknowledgment**
(optional), then **References**.  See chapter 8 in the suggested readings for
the complete details. We will also use classroom lectures to clarify how to
prepare these sections of your report.

Report length: 6 pages (formatted as per the template).

## The Proposal
The proposal is an early draft of the report.  Most of the proposal is the
**Introduction** (see above).

Include a **Method** section and the customary sub-sections (see template).
Since the evaluation is not yet conducted, write the Method section in the
future tense (e.g., "Eight participants will be recruited from...").  The goal
in the Method section is to tell the reader what you plan to do.  Use figures or
other visual aides to help guide the discussion.

Include sections for the **Abstract**, **Results and Discussion**, and
**Conclusions**, but just note "To be added later."

The proposal includes a **References** section.  Include at least four (4)
publications.  Make sure all references are cited in the body of the proposal.

Tip: Take the time to properly format the citations and references.  Consult the
template as well as section 8.3.2 ("Citations and References") in the suggested
readings.

Proposal length: 3 pages (formatted as per the template).

## Preparation

Prepare your proposal report by first downloading this
[template](http://www.eecs.yorku.ca/course_archive/2014-15/W/4443/template.doc)
and renaming it proposal.doc.  Read the template and follow the instructions, re
formatting, citing, referencing, writing style, etc.  Edit directly in the
renamed template using a word processor such as Microsoft __Word__ or The
Document Foundation's __LibreOffice__.  When finished, make a PDF copy of the
report.  Follow the submission instructions below.

After you submit the proposal (on March 8), make a copy of the file and name it
project.doc. Edit directly into this document to prepare the final project
report.

For the code, zip the files for the Android project into projectlogin.zip, where
login is your Prism login ID.

## Submission
Submit your work by logging on to Prism and using the "submit" command.

Proposal (by March 8):

```
    submit 4443 proposal proposal.pdf
```

Final project (by April 12):

```
    submit 4443 project project.pdf          (the report)
    submit 4443 project projectlogin.zip     (Android project source files)
```

Alternatively, you can submit using the web:
[click here](https://webapp.eecs.yorku.ca/submit/).

Good luck.
