
package acorn.userManagement;
import com.octo.captcha.engine.image.gimpy.SimpleListImageCaptchaEngine;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;

/**
 * This file has been copied from JCaptcha tutorial
 * @author kuba
 */


public class CaptchaServiceSingleton {
    
    //private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService();

    //private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService(new FastHashMapCaptchaStore(), new BasicListGimpyEngine(), 180, 100000, 75000);
    private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService(new FastHashMapCaptchaStore(), new SimpleListImageCaptchaEngine(), 180, 100000, 75000);
    
    public static ImageCaptchaService getInstance(){
        return instance;
    }
}