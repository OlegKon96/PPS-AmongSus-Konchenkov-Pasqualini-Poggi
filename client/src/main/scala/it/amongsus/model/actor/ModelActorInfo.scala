package it.amongsus.model.actor

trait ModelActorInfo {

}

object ModelActorInfo{
  def apply(): ModelActorInfo = ModelActorInfoData()
}

case class ModelActorInfoData() extends ModelActorInfo