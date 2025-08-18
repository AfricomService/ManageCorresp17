package com.africom.correspmanage.service.mapper;

import com.africom.correspmanage.domain.Bordereaux;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Dossier;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.service.dto.BordereauxDTO;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.dto.DossierDTO;
import com.africom.correspmanage.service.dto.PjDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Corresp} and its DTO {@link CorrespDTO}.
 */
@Mapper(componentModel = "spring")
public interface CorrespMapper extends EntityMapper<CorrespDTO, Corresp> {
    @Mapping(target = "dossiers", source = "dossiers", qualifiedByName = "dossierIdSet")
    @Mapping(target = "bordereaux", source = "bordereaux", qualifiedByName = "bordereauxIdSet")
    @Mapping(target = "attachments", source = "attachments", qualifiedByName = "pjIdSet")
    @Mapping(target = "tasks", source = "tasks", qualifiedByName = "tasksIdSet")
    CorrespDTO toDto(Corresp s);

    @Mapping(target = "removeDossiers", ignore = true)
    @Mapping(target = "removeBordereaux", ignore = true)
    @Mapping(target = "removeAttachments", ignore = true)
    @Mapping(target = "removeTasks", ignore = true)
    Corresp toEntity(CorrespDTO correspDTO);

    @Named("dossierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DossierDTO toDtoDossierId(Dossier dossier);

    @Named("dossierIdSet")
    default Set<DossierDTO> toDtoDossierIdSet(Set<Dossier> dossier) {
        return dossier.stream().map(this::toDtoDossierId).collect(Collectors.toSet());
    }

    @Named("bordereauxId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BordereauxDTO toDtoBordereauxId(Bordereaux bordereaux);

    @Named("bordereauxIdSet")
    default Set<BordereauxDTO> toDtoBordereauxIdSet(Set<Bordereaux> bordereaux) {
        return bordereaux.stream().map(this::toDtoBordereauxId).collect(Collectors.toSet());
    }

    @Named("pjId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PjDTO toDtoPjId(Pj pj);

    @Named("pjIdSet")
    default Set<PjDTO> toDtoPjIdSet(Set<Pj> pj) {
        return pj.stream().map(this::toDtoPjId).collect(Collectors.toSet());
    }

    @Named("tasksId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TasksDTO toDtoTasksId(Tasks tasks);

    @Named("tasksIdSet")
    default Set<TasksDTO> toDtoTasksIdSet(Set<Tasks> tasks) {
        return tasks.stream().map(this::toDtoTasksId).collect(Collectors.toSet());
    }
}
