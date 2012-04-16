package code.snippet

import code.model.User
import net.liftweb.common.{Full, Failure, Empty}
import net.liftweb.util.Helpers._
import code.config.Site
import code.lib.AppHelpers
import xml.Text

/**
 * @author Anton Chebotaev
 *         Owls Proprietary
 */


object UserSnippet extends AppHelpers {

  val owlUrl = "http://i293.photobucket.com/albums/mm46/smiley_foreva/Badge/owl.png"
  val failUrl = "http://sisyphus.ru/img/fail.png"

  def username = "* *" #> {
    User.currentUser match {
      case Empty => "NONAME"
      case Failure(msg, _, _) => msg
      case Full(user) => user.username.is
    }
  }

  def logout = "* [href]" #> url(Site.logout)

}