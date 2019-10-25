package ru.vonabe.audiostreaming

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.vonabe.audiostreaming.only.AGApplication

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("ru.vonabe.audiostreaming", appContext.packageName)
    }

    @Test
    fun setTime(){
        println(AGApplication.getCurrentTime().toString())
    }

}
