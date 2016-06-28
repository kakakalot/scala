
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

    for {
      _d <- _update
      member <- reflectedInstance.symbol.toType.members
      if member.isPublic && (!member.isMethod || member.asMethod.isGetter)
    } yield {
      try {
        if (member.name == _d._1) {
          reflectedInstance.reflectMethod(member.asMethod)(_d._2)
        }
        val value = reflectedInstance.reflectMethod(member.asMethod)()
        println(member.name, value)
      }
      catch {
        case e:Exception =>
          e.printStackTrace()
      }

      Obj("", Some(""), new C, Seq(), Some(false))
    }

  }
}

case class Obj (a: String, b: Option[String], c: C, d:Seq[Long], e:Option[Boolean])

class C {
  def c: Int = 0
}
