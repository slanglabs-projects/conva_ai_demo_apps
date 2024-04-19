package `in`.slanglabs.androidpg.slang


import android.app.Application
import `in`.slanglabs.androidpg.App
import `in`.slanglabs.androidpg.Repository
import `in`.slanglabs.conva.core.ConvaAI
import `in`.slanglabs.conva.core.Response
import `in`.slanglabs.conva.core.ResponseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SlangInterface(application: Application) {

    private var mApplication: Application = application
    private val isSingleShotResponse: Boolean = true

    init {
        mApplication = application
    }

    fun initialiseCopilot(assistantID: String, assistantKey: String, assistantVersion: String) {
        ConvaAI.init(assistantID, assistantKey,assistantVersion,mApplication)
    }

    fun startConversation(text: String) {
        if (isSingleShotResponse) singleShotResponse(text)
        else streamResponse(text)
    }

    private fun singleShotResponse(text: String) {
        val repository: Repository = (mApplication as App).getRepository()
        CoroutineScope(Dispatchers.IO).launch {
            val response = ConvaAI.invokeCapability(
                input = text
            )
            val message = response.message
            repository.sendResponse(message);
        }
    }

    private fun streamResponse(text: String) {
        val repository: Repository = (mApplication as App).getRepository()
        CoroutineScope(Dispatchers.IO).launch {
            ConvaAI.invokeCapability(
                input = text,
                listener = object : ResponseListener {
                    override fun onResponse(response: Response) {
                        val message = response.message
                        repository.sendResponse(message);
                    }

                    override fun onError(e: Exception) {
                        // Handle error
                    }
                }
            )
        }
    }
}