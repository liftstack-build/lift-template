package code.lib

import scala.xml._

import net.liftweb._
import common._
import http.{S, NoticeType}
import json._
import sitemap.Menu
import util.CssSel
import util.Helpers._

import org.bson.types.ObjectId

trait AppHelpers {

  def url(menu: Menu) = S.contextPath + menu.loc.calcDefaultHref

}
