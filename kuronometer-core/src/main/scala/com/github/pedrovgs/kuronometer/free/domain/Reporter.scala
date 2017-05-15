package com.github.pedrovgs.kuronometer.free.domain

sealed trait Report
case object LocalReport extends Report
case object RemoteReport extends Report

case class Config(platform: Platform.Value, reportProjectInfo: Boolean, reportDataRemotely: Boolean, verbose: Boolean)

