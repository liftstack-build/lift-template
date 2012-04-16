package code.snippet.admin

import code.config.Site
import code.model._


import net.liftweb._
import common.{Empty, Failure, Full}
import http.js.JsCmds
import http.js.JsCmds.Alert
import http.{LiftScreen, S}
import sitemap.Menu
import util.FieldError
import util.Helpers._
import code.lib.{AppHelpers, Gravatar}
import xml.NodeSeq

/**
 * @author Anton Chebotaev
 *         Owls Proprietary
 */


trait ModalScreen extends LiftScreen with AppHelpers {

  def finishCaption = "Ok"
  def cancelCaption = "Cancel"
  def openCaption = "Open"

  def divId = FormGUID.get

  override def allTemplatePath = "templates-hidden" :: "modal-screen" :: Nil
  override def renderHtml = ("form [class+]" #> "form-horizontal" & "label [class+]" #> "control-label")(super.renderHtml())
  override protected def wrapInDiv(in: NodeSeq) = super.wrapInDiv(in) % ("class" -> "modal fade")

  override val finishButton = <button class="btn btn-primary">{ finishCaption }</button>
  override val cancelButton = <button class="btn">{ cancelCaption }</button>

  protected def modalButton = <a class="btn" data-toggle="modal" href={ "#%s" format divId }>{ openCaption }</a>

  override def dispatch = {
    case "modalHref"   => "* [href]" #> "#%s".format(divId) & "* [data-toggle]" #> "modal"
    case "modalButton" => "*" #> modalButton
    case other         => super.dispatch(other)
  }

}

object RegisterScreen extends ModalScreen {

  object userVar extends ScreenVar(User.regUser.is)

  override def screenTop = Full(<h3>{ "Welcome" }</h3>)
  override def finishCaption = "Register"
  override def cancelCaption = "Cancel"

  addFields(() => userVar.is.registerScreenFields)

  override def calcAjaxOnDone = {
    val email = userVar.email.is
    User.findByEmail(email) match {
      case Full(user) => S.error("User with this email already exists")
      case Failure(msg, _, _) => S.error(msg)
      case Empty => {
        val user = userVar.is
        user.password.hashIt
        user.save
        User.logUserIn(userVar, true)
        User.createExtSession(userVar.id.is)
        S.notice("Thanks for signing up!")
        JsCmds.RedirectTo(Referer.get, () => S.appendNotices(S.getAllNotices))
      }
    }
  }

  protected def finish() {
//    emailField.

    println("done")
//    S.notice("Email = " + emailField.is)
//    S.redirectTo(url(Site.home))
//    redirectBack()
  }
}

object LoginScreen extends ModalScreen {

  override def screenTop = Full(<h3>{ "Welcome back" }</h3>)
  override def finishCaption = "Login"
  override def cancelCaption = "Cancel"

  val mailField = field("Email", "")
  val passField = password("Password", "")

  override def validations = auth _ :: super.validations

  def auth(): Errors = {
    val email = mailField.is
    val pass = passField.is

    User.findByEmail(email) match {
      case Empty => List(FieldError(mailField, "There is no userVar with such email"))
      case Full(user) if (!user.password.isMatch(pass)) => List(FieldError(passField, "Wrong password"))
      case _ => List.empty
    }
  }

  override def calcAjaxOnDone = {
    User.findByEmail(mailField.is) match {
      case Full(user) => {
        User.logUserIn(user, true)
        User.createExtSession(user.id.is)
        S.seeOther(url(Site.home))
      }
      case _ => {
        S.notice("Unknown error")
      }
    }
  }

  protected def finish() { }
}
