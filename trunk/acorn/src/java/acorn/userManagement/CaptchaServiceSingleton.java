
package acorn.userManagement;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/**
 * This file has been copied from JCaptcha tutorial
 * @author kuba
 */


public class CaptchaServiceSingleton {
    
    private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService();
    
    public static ImageCaptchaService getInstance(){
        return instance;
    }
}