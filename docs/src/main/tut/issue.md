---
layout: docs
title: Issue API
---

# Issue API

Github4s supports the [Issue API](https://developer.github.com/v3/issues/). As a result,
with github4s, you can:

- [create an issue](#create-an-issue)
- [edit an issue](#edit-an-issue)
- [list issues](#list-issues)
- [search issues](#search-issues)

The following examples assume the following imports and token:

```tut:silent
import github4s.Github
import github4s.Github._
import github4s.jvm.Implicits._
import scalaj.http.HttpResponse
// if you're using ScalaJS, replace occurrences of HttpResponse by SimpleHttpResponse
//import github4s.js.Implicits._
//import fr.hmil.roshttp.response.SimpleHttpResponse

val accessToken = sys.env.get("GITHUB4S_ACCESS_TOKEN")
```

They also make use of `cats.Id` but any type container implementing `MonadError[M, Throwable]` will
do.

Support for `cats.Id`, `cats.Eval` and `Future` (the only supported option for scala-js) are
provided out of the box when importing `github4s.{js,jvm}.Implicits._`.

## Create an issue

You can create an issue using `createIssue`, it takes as arguments:

- the repository coordinates (owner and name of the repository)
- the content of the issue (title and body)
- other optional parameters: milestone id, labels and assignees which are only taken into account
if you have push access to the repository

To create an issue:

```scala
val createIssue =
  Github(accessToken).issues.createIssue("47deg", "github4s", "Github4s", "is awesome")

createIssue.exec[cats.Id, HttpResponse[String]]() match {
  case Left(e) => println("Something went wrong: s{e.getMessage}")
  case Right(r) => println(r.result)
}
```

The `result` on the right is the created [Issue][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/#create-an-issue) for full reference.

## Edit an issue

You can edit an existing issue using `editIssue`, it takes as arguments:

- the repository coordinates (owner and name of the repository)
- the issue id
- the updated state of the issue (open or closed)
- the edited content of the issue (title and body)
- other optional parameters: milestone id, labels and assignees which are only taken into account
if you have push access to the repository

To edit an issue:

```scala
val editIssue =
  Github(accessToken).issues.editIssue("47deg", "github4s", 1, "open", "Github4s", "is still awesome")

editIssue.exec[cats.Id, HttpResponse[String]]() match {
  case Left(e) => println("Something went wrong: s{e.getMessage}")
  case Right(r) => println(r.result)
}
```

the `result` on the right is the edited [Issue][issue-scala].

See [the API doc](https://developer.github.com/v3/issues/#edit-an-issue) for full reference.

## List issues 

You can also list issues for a repository through `listIssues`, it take as arguments:

- the repository coordinates (owner and name of the repository)

To list the issues for a repository:

```tut:silent
val listIssues = Github(accessToken).issues.listIssues("47deg", "github4s")

listIssues.exec[cats.Id, HttpResponse[String]]() match {
  case Left(e) => println("Something went wrong: s{e.getMessage}")
  case Right(r) => println(r.result)
}
```

The `result` on the right is the corresponding [List[Issue]][issue-scala]. Note that it will
contain pull requests as Github considers pull requests as issues.

See [the API doc](https://developer.github.com/v3/issues/#list-issues-for-a-repository)
for full reference.

## Search issues

Lastly, you can also search issues all across Github thanks to `searchIssues`, it takes as
arguments:

- a query string (the URL encoding is taken care of by Github4s)
- a list of [SearchParam](https://github.com/47deg/github4s/blob/master/github4s/shared/src/main/scala/github4s/free/domain/SearchParam.scala)

Let's say we want to search for the Scala bugs (<https://github.com/scala/bug>) which contain
the "existential" keyword in their title:

```tut:silent
import github4s.free.domain._
val searchParams = List(
  OwnerParamInRepository("scala/bug"),
  IssueTypeIssue,
  SearchIn(Set(SearchInTitle))
)
val searchIssues = Github(accessToken).issues.searchIssues("existential", searchParams)

searchIssues.exec[cats.Id, HttpResponse[String]]() match {
  case Left(e) => println("Something went wrong: s{e.getMessage}")
  case Right(r) => println(r.result)
}
```

The `result` on the right is a [SearchIssuesResult][issue-scala].

See [the API doc](https://developer.github.com/v3/search/#search-issues) for full reference.

[issue-scala]: https://github.com/47deg/github4s/blob/master/github4s/shared/src/main/scala/github4s/free/domain/Issue.scala