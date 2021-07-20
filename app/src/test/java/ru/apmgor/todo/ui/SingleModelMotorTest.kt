package ru.apmgor.todo.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import ru.apmgor.todo.MainDispatcherRule
import ru.apmgor.todo.repo.ToDoModel
import ru.apmgor.todo.repo.ToDoRepository

@ExperimentalCoroutinesApi
class SingleModelMotorTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(paused = true)

    private val testModel = ToDoModel("this is a test")
    private val repo: ToDoRepository = mock {
        on { find(testModel.id) } doReturn flowOf(testModel)
    }
    private lateinit var underTest: SingleModelMotor

    @Before
    fun setUp() {
        underTest = SingleModelMotor(repo, testModel.id)
    }

    @Test
    fun `initial state`() {
        val observer = underTest.states.test()

        mainDispatcherRule.dispatcher.runCurrent()
        observer.awaitValue().assertValue { it.item == testModel }
    }

    @Test
    fun `actions pass through to repo`() {
        val replacement = testModel.copy("whatevs")

        underTest.save(replacement)
        mainDispatcherRule.dispatcher.runCurrent()
        runBlocking { verify(repo).save(replacement) }

        underTest.delete(replacement)
        mainDispatcherRule.dispatcher.runCurrent()
        runBlocking { verify(repo).delete(replacement) }
    }
}