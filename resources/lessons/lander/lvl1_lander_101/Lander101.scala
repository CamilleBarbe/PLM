package lessons.lander.lvl1_lander_101

import plm.core.model.lesson.ExerciseTemplated
import lessons.lander.universe.DelegatingLanderWorld
import lessons.lander.universe.Point
import lessons.lander.universe.LanderEntity
import lessons.lander.universe.Configurations._
import Math.PI
import scala.collection.JavaConversions._
import lessons.lander.universe.LanderWorld
import plm.universe.World

class Lander101() extends ExerciseTemplated("Lander101") {
  tabName = "Lander"
  setup(SIMPLE_TERRAIN_TRIVIAL_CONFIG)
}
