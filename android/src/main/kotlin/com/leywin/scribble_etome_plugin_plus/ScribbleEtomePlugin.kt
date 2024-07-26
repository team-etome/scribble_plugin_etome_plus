package com.leywin.scribble_etome_plugin_plus


import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


class ScribbleEtomePlugin : FlutterPlugin, MethodCallHandler {

  private lateinit var channel: MethodChannel

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "canvas_etome_options")
    channel.setMethodCallHandler(this)

    // Register the platform view factory
    flutterPluginBinding.platformViewRegistry
      .registerViewFactory("leywin/etome/scribble_etome", ScribbleEtomeViewFactory(channel))
  }

  override fun onMethodCall(call: MethodCall, result: Result) {

  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}