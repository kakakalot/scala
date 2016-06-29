
import java.io.File

import com.sksamuel.scrimage.Image
import org.json4s.jackson.Serialization
import play.api.libs.json._
import play.json.extra.Jsonx

import scala.reflect.runtime.universe._
import scala.reflect.runtime.currentMirror

/**
  * Created by thien.tnc on 6/28/2016.
  */

object Debug extends App {

  implicit def reflector(ref: AnyRef) = new {
    def getV(name: String): Any = ref.getClass.getMethods.find(_.getName == name).get.invoke(ref)
    def setV(name: String, value: Any): Unit = ref.getClass.getMethods.find(_.getName == name).get.invoke(ref, value.asInstanceOf[Object])
  }

  override def main(args: Array[String]) {

    val _update = Map("d" -> Seq(1,2,3))
    val obj = Obj("a1", Some("b1"), new C, Seq(1,2, Long.MaxValue), Some(false))
    val reflectedInstance = currentMirror.reflect(obj)
//    obj.setV("d", Seq(1,2,3))
//    obj.setV("a", "abc")

    var _map: Map[String, Any] = Map()
    val result = for {
      _d <- _update
      member <- reflectedInstance.symbol.toType.members
      if member.isPublic && (!member.isMethod || member.asMethod.isGetter)
    } yield {
      try {
        if (member.name == _d._1) {
          reflectedInstance.reflectMethod(member.asMethod)(_d._2)
        }

        val value = reflectedInstance.reflectMethod(member.asMethod)()
        _map += (member.name.toString -> value)
      }
      catch {
        case e:Exception =>
          e.printStackTrace()
      }
    }

    implicit val cFormat = Jsonx.formatCaseClass[C]
    implicit val formats = org.json4s.DefaultFormats

    implicit def writeAny: Writes[Map[String, Any]] = new Writes[Map[String, Any]] {
      override def writes(o: Map[String, Any]): JsValue = {
        var _sep:Seq[(String, JsValue)] = List()
        for (key <- o.keySet) {
          val value:JsValue = o.getOrElse(key, None) match {
            case C(0) => Json.toJson(o.get(key).get.asInstanceOf[C])
            case v:Option[Any] => v.get match {
              case s:String => JsString(s)
              case b:Boolean => JsBoolean(b)
            }
            case _ => JsNull
          }

//          println(value)
          _sep++= Seq(key -> value.asInstanceOf[JsValue])
        }

        JsObject(_sep)
      }
    }

    println(Serialization.write(_map))
    println(Json.toJson(_map))
//    Obj("", Some(""), new C, Seq(), Some(false))
//    val a = result.fold {
//
//    }


//    val inImage = Image.fromFile(new File("D:\\screen_shot.jpg"))
//    println(s"w: ${inImage.width}, h: ${inImage.height}")

  }
}

case class Obj (a: String, b: Option[String]=None, c: C, d:Seq[Long], e:Option[Boolean])

case class C (c:Int = 0)
