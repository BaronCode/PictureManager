package com.picman.picman.PicturesMgmt;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pictures")
public class Picture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)                         private     long            id;
    @Column(nullable = false, unique = true)                                        private     String          path;
    @Column(nullable = false, length = 4)                                           private     String          ext;
    @Column(nullable = false)                                                       private     LocalDateTime   dateadded;
    @Column(nullable = false)                                                       private     boolean         protection;
                                                                                    private     BigDecimal      sizekb;
                                                                                    private     LocalDateTime   shotat;
                                                                                    private     Integer         height;
                                                                                    private     Integer         width;
                                                                                    private     BigDecimal      aperture;
                                                                                    private     Integer         iso;
                                                                                    private     Short           focallength;
                                                                                    private     Integer         exposurenum;
                                                                                    private     Integer         exposureden;
                                                                                    private     String          cameramodel;
                                                                                    private     String          photographer;

    public Picture(
        String          path,
        String          ext,
        LocalDateTime   dateadded,
        boolean         protection,
        BigDecimal      sizekb,
        LocalDateTime   shotat,
        Integer         height,
        Integer         width,
        BigDecimal      aperture,
        Integer         iso,
        Integer         exposurenum,
        Integer         exposureden,
        Short           focallength,
        String          cameramodel
    ) {
        this.path = path;
        this.ext = ext;
        this.dateadded = dateadded;
        this.protection = protection;
        this.sizekb = sizekb;
        this.shotat = shotat;
        this.height = height;
        this.width = width;
        this.aperture = aperture;
        this.iso = iso;
        this.exposurenum = exposurenum;
        this.exposureden = exposureden;
        this.focallength = focallength;
        this.cameramodel = cameramodel;
    }

    @PrePersist
    public void prePersist() {
        if (dateadded == null) {
            dateadded = LocalDateTime.now();
        }
    }

    public String getFormattedDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
