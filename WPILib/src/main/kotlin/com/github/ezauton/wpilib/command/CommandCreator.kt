package com.github.ezauton.wpilib.command

import com.github.ezauton.core.action.Action
import edu.wpi.first.wpilibj.command.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Creates a action from an [Action]. This allows for abstraction and use of commands in simulators.
 */
class CommandCreator(private val action: Action) : Command() {
  private lateinit var job: Job
  override fun initialize() {
    job = GlobalScope.launch {
      action.run()
    }
  }

  override fun execute() {
    // nothing to execute... everything is in the thread.
  }

  override fun isFinished(): Boolean {
    return !job.isActive
  }

  override fun interrupted() {
    job.cancel()
  }

}
