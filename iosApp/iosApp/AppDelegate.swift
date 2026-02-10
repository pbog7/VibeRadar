import UIKit
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        
        // This is what triggers the Firebase logs you're looking for
        FirebaseApp.configure()
        print("Vibe Radar: Firebase Engine Started")
        
        return true
    }
}
