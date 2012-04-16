package code.config

import net.liftweb._
import common._
import http._
import sitemap._
import sitemap.Loc._

import net.liftmodules.mongoauth.Locs
import code.model.User

/**
 * @author Anton Chebotaev
 *         Owls Proprietary
 */

object Site {

  object MenuGroup {
    val TopBar   = LocGroup("topbar")
    val Account  = LocGroup("account")
    val Admin    = LocGroup("admin")
  }
  
  object AuthLocs extends Locs {
    override protected def logoutLocParams =     MenuGroup.Account :: Hidden :: super.logoutLocParams
    override protected def loginTokenLocParams = MenuGroup.Account :: Hidden :: super.loginTokenLocParams
  }

  // locations (menu entries)
  val home = Menu("Home") / "index" >>
    MenuGroup.TopBar
  
  val login = AuthLocs.buildLoginTokenMenu
  val logout = AuthLocs.buildLogoutMenu

  private val profileParamMenu = Menu.param[User]("User", "Profile",
    User.findByUsername _,
    _.username.is
  ) / "profile" >> Loc.CalcValue(() => User.currentUser) >> MenuGroup.Account

  private def menus = List(
    home,

    Menu.i("Register") / "register" >> AuthLocs.RequireNotLoggedIn,
    logout,
    profileParamMenu,

    Menu("Admin") / "admin" submenus (
      Menu("Item") / "admin" / "item" >> Hidden
      ) >> MenuGroup.TopBar,

    Menu("About")   / "about"    >> MenuGroup.TopBar,
    Menu("Test")    / "test"     >> MenuGroup.TopBar,//  >> Snippet("TestAdminScreen", TestAdminScreen),

    Menu("Throw")   / "throw"    >> Hidden,
    Menu("Error")   / "error"    >> Hidden,
    Menu("404")     / "404"      >> Hidden
  )

  /*
  * Return a SiteMap needed for Lift
  */
  def sitemap: SiteMap = SiteMap(menus:_*)

}