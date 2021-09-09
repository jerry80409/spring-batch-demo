package com.example.batchdemo.job;

import com.example.batchdemo.job.persisted.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerProcessor implements ItemProcessor<User, User> {

    /**
     * Process the provided item, returning a potentially modified or new item for continued
     * processing.  If the returned result is {@code null}, it is assumed that processing of the
     * item should not continue.
     *
     * A {@code null} item will never reach this method because the only possible sources are:
     * <ul>
     *     <li>an {@link ItemReader} (which indicates no more items)</li>
     *     <li>a previous {@link ItemProcessor} in a composite processor (which indicates a filtered item)</li>
     * </ul>
     *
     * @param item to be processed, never {@code null}.
     * @return potentially modified or new item for continued processing, {@code null} if processing
     * of the provided item should not continue.
     * @throws Exception thrown if exception occurs during processing.
     */
    @Override
    public User process(User item) throws Exception {
        val user = User.builder()
            .id(item.getId())
            .name(item.getName().toUpperCase())
            .email(item.getEmail().toUpperCase())
            .build();
        log.info("user process demo: {}", user.getName());
        return user;
    }
}
