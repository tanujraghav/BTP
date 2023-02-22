package jnu.se.btp.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Component
public class OTPService {

    private final LoadingCache<String, Integer> otpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(420, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) throws Exception {
                    return null;
                }
            });

    public int generateOTP(String key) {
        int OTP = new Random().nextInt(899999) + 100000;
        otpCache.put(key, OTP);
        return OTP;
    }

    @SneakyThrows
    public int readOTP(String key) {
        return otpCache.getIfPresent(key);
    }

    public void invalidateOTP(String key) {
        otpCache.invalidate(key);
    }

}
