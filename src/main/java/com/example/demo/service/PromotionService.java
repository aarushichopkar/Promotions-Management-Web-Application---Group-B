package com.example.demo.service;

import com.example.demo.dto.EmailRequest;
import com.example.demo.dto.EmailResponse;
import com.example.demo.model.Promotion;
import com.example.demo.quartz.Job.EmailJob;
import com.example.demo.repository.PromotionRepo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.midi.Track;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PromotionService {

    private final PromotionRepo promotionRepo;

    @Autowired
    public PromotionService(PromotionRepo promotionRepo){
        this.promotionRepo = promotionRepo;
    }

    @Autowired
    private Scheduler scheduler;

    // Scheduling and Email Services
    public String generate_email(Promotion promotion){
        // Start Time Scheduler
        String msg = "I am pleased to inform you that your promotion is now live on our website! \n We have successfully implemented all the details as discussed, and it is prominently featured to attract the maximum number of visitors.\n" +
                " We have ensured that the promotion is highlighted on the homepage and relevant sections" +
                " of our site to increase visibility and engagement. \n Please take a moment to review it" +
                " and let us know if there are any adjustments or additions you would like to make.\n";
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("rishavkundra981@gmail.com");
        emailRequest.setSubject("Your Promotion is Now Live on Our Website!");
        emailRequest.setBody(msg);
        emailRequest.setPromotion_id(promotion.getId());
        emailRequest.setDateTime(promotion.getStart_time());
        emailRequest.setTimeZone(promotion.getTimeZone());
        ResponseEntity<EmailResponse> er = scheduleEmail(emailRequest);

        // End-Time Scheduler
        String msg_end = "Promotion ended! Thank u!";
        EmailRequest emailRequest_end = new EmailRequest();
        emailRequest_end.setEmail("rishavkundra981@gmail.com");
        emailRequest_end.setSubject("Your Promotion ended");
        emailRequest_end.setBody(msg_end);
        emailRequest_end.setPromotion_id(promotion.getId());
        emailRequest_end.setDateTime(promotion.getEnd_time());
        emailRequest_end.setTimeZone(promotion.getTimeZone());
        ResponseEntity<EmailResponse> er_end = scheduleEmail(emailRequest_end);

        System.out.println(er.getBody().getMessage());
        System.out.println(er_end.getBody().getMessage());

        if(er.getBody().isSuccess() && er.getBody().isSuccess()){
            return "Promotion Scheduled Successfully";
        }
        else {
            return "unable to Schedule Promotion";
        }
    }
    public ResponseEntity<EmailResponse> scheduleEmail(EmailRequest emailRequest){
        try{
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())){
                EmailResponse emailResponse = new EmailResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
            }
            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);

            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse emailResponse = new EmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(),
                    "Email Scheduled Successfully!");

            return ResponseEntity.ok(emailResponse);

        } catch (SchedulerException se){
            log.error("Error while Scheduling email: ", se);
            EmailResponse emailResponse = new EmailResponse(false, "Error while Scheduling");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);

        }
    }
    private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest){
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());
        jobDataMap.put("promotion_id", scheduleEmailRequest.getPromotion_id());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


    public Promotion addPromotion(Promotion promotion) {
        promotion.setActive(false);
        return promotionRepo.save(promotion);
    }

    public List<Promotion> find_all_promotion() {
        return (List<Promotion>) promotionRepo.findAll();
    }

    public Promotion updatePromotion(long promotionId, Promotion promotion) {
        Optional<Promotion> p = promotionRepo.findById(promotionId);
        if(p.isEmpty()){
            return null;
        }
        Promotion pro = p.get();
        pro.setPromotionType(promotion.getPromotionType());
        pro.setEnd_time(promotion.getEnd_time());
        pro.setManager_id(promotion.getManager_id());
        pro.setOwner_id(promotion.getOwner_id());
        pro.setDiscount_rate(promotion.getDiscount_rate());
        pro.setActive(promotion.isActive());
        pro.setApplicableProducts(promotion.getApplicableProducts());
        promotionRepo.save(pro);
       return pro;
    }
}
