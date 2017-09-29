package com.abhi

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.{ConsumerMessage, ConsumerSettings, Subscriptions}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import akka.kafka.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ASrivastava on 9/28/17.
  */

object AkkaStreamskafka extends App {

   // producer settings
   implicit val system = ActorSystem()
   implicit val actorMaterializer = ActorMaterializer()
   val consumerSettings = ConsumerSettings(system, Some(new ByteArrayDeserializer()), Some(new StringDeserializer()))
      .withBootstrapServers("devbox:9092")
      .withGroupId("abhi")
      .withClientId("abhi")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
      .withWakeupTimeout(1 minutes)
      .withCloseTimeout(10 seconds)
      .withCommitTimeout(10 seconds)
      .withDispatcher("akka.kafka.default-dispatcher")
      .withMaxWakeups(10)
      .withPollTimeout(10 seconds)
      .withPollInterval(1 second)

   val future = Consumer
      .committableSource(consumerSettings, Subscriptions.topics("test"))
   .map(msg => println(msg)).runWith(Sink.ignore)
   future.onComplete(_ => system.terminate())
   Await.result(system.whenTerminated, Duration.Inf)
}
