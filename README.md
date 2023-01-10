# Review/Assignment Workflow

## Introduction

Review Workflow or Assignment Workflow is a workflow which enables the user to request a review after a document change and assign this review step to a group.

The assigned group's members are the only ones that can accept or reject the request. The request can be cancelled by the authorized user or an admin.

## Release Notes

|     CMS      |  Version   |
|:------------:|:----------:|
|     11.1     |   0.1.x    |
| :----------: | :--------: |
|     12.0     |   0.2.x    |
| :----------: | :--------: |
|    14.2.0    |   1.1.0    |
| :----------: | :--------: |
|    15.2.0    |   2.0.x    |

See [CHANGES](CHANGES.md) for release notes.

For release processes, see [Hippo Forge Release Process](https://onehippo-forge.github.io/release-process.html).

## Install

* Make sure you have the forge Maven2 repository reference in the root pom.xml of your project.

```xml
<repositories>
 
  <!-- SNIP -->
 
  <repository>
    <id>hippo-maven2</id>
    <name>Hippo Maven 2 Repository</name>
    <url>https://maven.onehippo.com/maven2-forge//</url>
  </repository>
 
  <!-- SNIP -->
 
</repositories>
```

* cms-dependencies pom.xml

```xml
        <dependency>
            <groupId>org.bloomreach.forge.review-workflow</groupId>
            <artifactId>review-workflow-frontend</artifactId>
            <version>${review-workflow.version}</version>
        </dependency>

        <dependency>
            <groupId>org.bloomreach.forge.review-workflow</groupId>
            <artifactId>review-workflow-repository</artifactId>
            <version>${review-workflow.version}</version>
        </dependency>
```



  
* Shared dependencies (Can be deployed in cms.war by adding dependency in cms/pom.xml instead if *request online review* not used)

    * root pom.xml:

    ```xml
    ...
  <!-- pom.xml's dependency section (not dependencyManagement) -->
            <dependencies>
                <dependency>
                    <groupId>org.bloomreach.forge.review-workflow</groupId>
                    <artifactId>review-workflow-shared</artifactId>
                    <version>${review-workflow.version}</version>
                </dependency>
            </dependencies>
  
  <!---cargo profile -->
            ....
             <container>
                <systemProperties>
                </systemProperties>
                 <dependencies>
                     <dependency>
                         <groupId>org.bloomreach.forge.review-workflow</groupId>
                         <artifactId>review-workflow-shared</artifactId>
                         <classpath>shared</classpath>
                      </dependency>
                  </dependencies>
              </container>
    ....
    ```

    * distribution:

    ```xml
    ...
           <dependencySets>
               <dependencySet>
                 <useProjectArtifact>false</useProjectArtifact>
                 <outputDirectory>shared/lib</outputDirectory>
                 <scope>provided</scope>
                 <includes>
                   <include>org.onehippo.cms7:hippo-addon-review-workflow-shared</include>
    ....
    ```

* (Optional for request online review) site pom.xml

```xml
        <dependency>
            <groupId>org.bloomreach.forge.review-workflow</groupId>
            <artifactId>review-workflow-hst</artifactId>
            <version>${review-workflow.version}</version>
        </dependency>
```

* (Optional for request online review) hst-config.properties


```properties
     reviewworkflowuser.repository.address = vm://
     reviewworkflowuser.repository.user.name = reviewonlineuser
     reviewworkflowuser.repository.pool.name = reviewworkflowuser
     reviewworkflowuser.repository.password =
```


## Build/Run Demo

First, build/install the plugin itself locally:

```bash
    $ mvn clean install
```

Next, move to demo folder and build/run it:

```bash
    $ cd demo
    $ mvn clean verify
    $ mvn -Pcargo.run
```

## Configure

## Review Request Frontend Plugin

See /hippo:configuration/hippo:workflows/default/handle/frontend:renderer/review

| Setting  | Description  |
|---|---|
|requestReview.enabled   | Enable review request plugin button   | 
|onlineRequestReview.enabled  | Enable optional online review request plugin button   | 
|multipleReviewRequests.enabled   | Enable creating multiple review requests   | 
|multipleReviewRequests.limit   | Max number of review requests that can be created by anyone in total   | 
|acceptReview.enabled  | Enable accept a review  | 
|cancelReview.enabled   | Enable canceling a review   | 
|rejectReview.enabled  | Enable rejecting a  review  | 
|dropReview.enabled | Enable dropping a review  | 
|internal.assign.list.path| Value list that populates the assign dropdown. Will always be taken into account |
 
### For Online Review (Optional feature):

For the online review to work there are several things necessary to set in place.

For the demo project we have setup the following to work with the "hst" addon. Beware this is example configuration setup.

#### 1. Enable Request Review Online menu in CMS Workflow menu UI.

Set ```/hippo:configuration/hippo:workflows/default/handle/frontend:renderer/review/@onlineRequestReview.enabled``` to true (Boolean).

#### 2. Preview Mount with a distinct alias. Create one with alias "reviewworkflow" as an example.

#### 3. REST mount for retrieving the preview URL at /rest as an example

 ```yaml
    ...

/preview:
  jcr:primaryType: hst:mount
  jcr:uuid: cca9cdc1-77c1-4266-847f-2cc179c73a8f
  hst:alias: reviewworkflow
  hst:type: preview
```
```yaml
/rest:
  jcr:primaryType: hst:mount
  jcr:uuid: 3fb118d6-e6da-4d40-ae63-4bb2a37a29c0
  hst:alias: rest
  hst:ismapped: false
  hst:namedpipeline: JaxrsRestPlainPipeline
  hst:types: [rest]
```

#### 4. There are 2 value list available in the administration/Review Worklfow folder. These are the assignment list which appears when you do a review request.

 * internal assign list:

    This is an additional list of values you can add to the group list.

 * online request list

    This is an list of email addresses where the preview link can be send to.

It is important that the administration folder is present before adding the repository module to the CMS. Because these value lists are being bootstrapped.

#### 5. create a component through the hst configuration for the detail page of you reviewable document.
As an example add the org.onehippo.forge.reviewworkflow.hst.ExampleReviewWorkflowComponent as the componentclassname and reviewworkflow (bootstrapped in default) as your template

```yaml
/review-workflow:
  jcr:primaryType: hst:component
  jcr:uuid: 3faf19fb-a55b-45f5-94d9-658355fa8068
  hst:componentclassname: org.onehippo.forge.reviewworkflow.hst.ExampleReviewWorkflowComponent
  hst:template: reviewworkflow
```

dont forget to include the component on your detail page!

#### 6. There are 2 e-mail handling modules included in the demo project

* event-handler
* email-addon

these modules make sure there is an event listener in the CMS which listens explicitly to the request review event and sends a mail to a certain user.

example of online review mail:


```txt


******************************************************************
Sending the following email to k.salic@onehippo.com <k.salic@onehippo.com>
A review was requested for The medusa news, by user admin
Document link: http://localhost:8080/site/preview/news/2016/12/the-medusa-news.html?workflowId=450a142e-efc2-42ed-adfe-fd88b6d9f4b9
******************************************************************


 ```

These modules are not supported by Bloomreach and can be used as inspiration or only for demo purposes.

#### 7. Use FakeSmtp as SMTP server for emails

* Download  FakeSMTP from: [https://github.com/Nilhcem/FakeSMTP](https://github.com/Nilhcem/FakeSMTP)

* Run it as:

```bash
java -jar fakeSMTP-2.0.jar -s  -p 2525 -a 127.0.0.1
```
* Email module in demo project is already configured to use the above information

## Use

![screenshot1](/images/screenshot1.png "Logo Title Text 1")
![screenshot2](/images/screenshot2.png "Logo Title Text 1")
![screenshot3](/images/screenshot3.png "Logo Title Text 1")
![screenshot4](/images/screenshot4.png "Logo Title Text 1")
![screenshot6](/images/screenshot6.png "Logo Title Text 1")
![screenshot7](/images/screenshot7.png "Logo Title Text 1")
![screenshot8](/images/screenshot8.png "Logo Title Text 1")

## Extend

The dropdown that contains assignable groups can be populated dynamically, based on currently logged in user and/or
the document in question. To achieve this:
* Add a custom spring bean definition to cms/src/main/resources/META-INF/hst-assembly/overrides in which you implement
org.bloomreach.forge.reviewworkflow.cms.reviewedactions.AssignableGroupsProvider 

```xml
  <!--keep the id, change the fully qualified class name-->
  <bean id="org.bloomreach.forge.reviewworkflow.cms.reviewedactions.AssignableGroupsProvider" class="com.bloomreach.your.implementation"/>
```

* Note that you could also pass a jcr session to this bean in the spring configuration



