package li.klass.fhem.appindex

import android.app.IntentService
import android.content.Intent
import li.klass.fhem.AndFHEMApplication
import li.klass.fhem.update.backend.DeviceListService
import org.slf4j.LoggerFactory
import javax.inject.Inject

class AppIndexIntentService : IntentService(AppIndexIntentService::class.java.name) {
    @Inject
    lateinit var deviceListService: DeviceListService
    lateinit var indexableCreator: IndexableCreator
    lateinit var firebaseIndexWrapper: FirebaseIndexWrapper

    override fun onCreate() {
        super.onCreate()
        val component = (application as AndFHEMApplication).daggerComponent
        component.inject(this)

        indexableCreator = IndexableCreator()
        firebaseIndexWrapper = FirebaseIndexWrapper()
    }

    public override fun onHandleIntent(intent: Intent?) {
        LOGGER.info("onHandleIntent - updating index")

        val deviceList = deviceListService.getAllRoomsDeviceList()
        val roomNames = deviceListService.getRoomNameList()

        val indexableRoomNames = roomNames.map { roomName -> indexableCreator.indexableFor(this, roomName) }
        val indexableDevices = deviceList.allDevices.map { device -> indexableCreator.indexableFor(this, device) }
        val indexables = indexableRoomNames + indexableDevices

        firebaseIndexWrapper.update(indexables)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AppIndexIntentService::class.java)
    }
}
