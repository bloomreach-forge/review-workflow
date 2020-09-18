# Review/Assignment Workflow

## 1.1.0

* Fix a bug where wicket session id was being used instead of jcr session
* Change signature of AssignableGroupsProvider so that the document context can be acquired to provide a more flexible
dropdown list

## 1.0.1

* DocumentReviewWorkflowImpl makes use of the internal locking mechanism to avoid concurrency issues

## 1.0.0

* Upgrade to v14
* Refactoring
* Upgrading scxml definition to suit v14.2.0
* Simplifying CustomDocumentHandle
* Fix online review in demo project: Casting of proxy workflow instance to custom workflow instance was throwing errors
* Documentation fixes

## 0.1.2

#### Bug Fixes

- Move JCR query to retrieve all the user names from dialog UI to a service registered by a daemon module because some users (such as author) are not authorized to retrieve users from repository by default.

## 0.1.1

#### Improvements

- Make *Request Online Review* menu optional since it's limited and requiring custom implementations.

## 0.1.0 : Initial Version

There are a few features in the initial version

* request review
assign specifically
* request online review (limited support)
   * assign specifically
   * ~~send mail~~ (not included)
   * hst workflow
* accept review
* reject review
   * add reason
   * view reason
* cancel review
* todo plugin 
   * request for review notification
   * rejection reason notification
   * rejection todo

Not in scope :

* email module, send emails

An example in the demo project is included of sending emails
