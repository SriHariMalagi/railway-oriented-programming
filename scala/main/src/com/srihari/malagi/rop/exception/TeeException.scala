package com.srihari.malagi.rop.exception

class TeeException(message: String, e: Throwable) extends Throwable(message, e)

object TeeException {
  def apply(message: String, e: Throwable): TeeException = new TeeException(message, e)

  def apply(e: Throwable): TeeException = new TeeException("Failed to execute block, propagating exception", e)
}