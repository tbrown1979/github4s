/*
 * Copyright 2016-2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.free.algebra

import cats.data.NonEmptyList
import cats.free.{Free, Inject}
import github4s.GithubResponses.GHResponse
import github4s.free.domain._

/**
 * Repositories ops ADT
 */
sealed trait RepositoryOp[A]

final case class GetRepo(
    owner: String,
    repo: String,
    accessToken: Option[String] = None
) extends RepositoryOp[GHResponse[Repository]]

final case class GetContents(
    owner: String,
    repo: String,
    path: String,
    ref: Option[String] = None,
    accessToken: Option[String] = None
) extends RepositoryOp[GHResponse[NonEmptyList[Content]]]

final case class ListCommits(
    owner: String,
    repo: String,
    sha: Option[String] = None,
    path: Option[String] = None,
    author: Option[String] = None,
    since: Option[String] = None,
    until: Option[String] = None,
    pagination: Option[Pagination] = None,
    accessToken: Option[String] = None
) extends RepositoryOp[GHResponse[List[Commit]]]

final case class ListContributors(
    owner: String,
    repo: String,
    anon: Option[String] = None,
    accessToken: Option[String] = None
) extends RepositoryOp[GHResponse[List[User]]]

final case class CreateRelease(
    owner: String,
    repo: String,
    tagName: String,
    name: String,
    body: String,
    targetCommitish: Option[String] = None,
    draft: Option[Boolean] = None,
    prerelease: Option[Boolean] = None,
    accessToken: Option[String] = None
) extends RepositoryOp[GHResponse[Release]]

/**
 * Exposes Repositories operations as a Free monadic algebra that may be combined with other Algebras via
 * Coproduct
 */
class RepositoryOps[F[_]](implicit I: Inject[RepositoryOp, F]) {

  def getRepo(
      owner: String,
      repo: String,
      accessToken: Option[String] = None
  ): Free[F, GHResponse[Repository]] =
    Free.inject[RepositoryOp, F](GetRepo(owner, repo, accessToken))

  def getContents(
      owner: String,
      repo: String,
      path: String,
      ref: Option[String] = None,
      accessToken: Option[String] = None
  ): Free[F, GHResponse[NonEmptyList[Content]]] =
    Free.inject[RepositoryOp, F](GetContents(owner, repo, path, ref, accessToken))

  def listCommits(
      owner: String,
      repo: String,
      sha: Option[String] = None,
      path: Option[String] = None,
      author: Option[String] = None,
      since: Option[String] = None,
      until: Option[String] = None,
      pagination: Option[Pagination] = None,
      accessToken: Option[String] = None
  ): Free[F, GHResponse[List[Commit]]] =
    Free.inject[RepositoryOp, F](
      ListCommits(owner, repo, sha, path, author, since, until, pagination, accessToken))

  def listContributors(
      owner: String,
      repo: String,
      anon: Option[String] = None,
      accessToken: Option[String] = None
  ): Free[F, GHResponse[List[User]]] =
    Free.inject[RepositoryOp, F](ListContributors(owner, repo, anon, accessToken))

  def createRelease(
      owner: String,
      repo: String,
      tagName: String,
      name: String,
      body: String,
      targetCommitish: Option[String] = None,
      draft: Option[Boolean] = None,
      prerelease: Option[Boolean] = None,
      accessToken: Option[String] = None
  ): Free[F, GHResponse[Release]] =
    Free.inject[RepositoryOp, F](
      CreateRelease(
        owner,
        repo,
        tagName,
        name,
        body,
        targetCommitish,
        draft,
        prerelease,
        accessToken))

}

/**
 * Default implicit based DI factory from which instances of the RepositoryOps may be obtained
 */
object RepositoryOps {

  implicit def instance[F[_]](implicit I: Inject[RepositoryOp, F]): RepositoryOps[F] =
    new RepositoryOps[F]

}
