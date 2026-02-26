package com.picman.picman.LoggingMgmt;

import com.picman.picman.UserMgmt.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "log")
public class Log {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)                         private     long            id;
    @Column(nullable = false)                                                       private     LocalDateTime   date;
                                                                                    private     String          pagename;
    @Column(nullable = false)                                                       private     String          classname;
    @ManyToOne @JoinColumn(name = "userid")                                           private     User            userid;
    @Column(nullable = false)                                                       private     String          description;

    public Log(LocalDateTime ldt, String pn, String cn, User u, String desc) {
        date = ldt;
        pagename = pn;
        classname = cn;
        userid = u;
        description = desc;
    }
}
