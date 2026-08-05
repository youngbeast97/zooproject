package com.zoo.zoo.repository.feedingerror;

import com.zoo.zoo.model.feeding.FeedingErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedingErrorLogRepository  extends JpaRepository<FeedingErrorLog,Long> {

}
