package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static utils.JpaUtils.EMPTY_DELETE_TOKEN;

@Entity
@ApiModel(value="FavoritesList")
@Where(clause = "deleteToken = '" + EMPTY_DELETE_TOKEN + "'")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "deleteToken"}))
public class FavoritesList {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String deleteToken = EMPTY_DELETE_TOKEN;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoritesList that = (FavoritesList) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
