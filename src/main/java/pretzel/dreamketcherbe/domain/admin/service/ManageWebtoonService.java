package pretzel.dreamketcherbe.domain.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.admin.dto.ManageWebtoonResDto;
import pretzel.dreamketcherbe.domain.admin.dto.UpdateWebtoonApprovalReqDto;
import pretzel.dreamketcherbe.domain.admin.dto.UpdateWebtoonStatusReqDto;
import pretzel.dreamketcherbe.domain.admin.entity.ApprovalStatus;
import pretzel.dreamketcherbe.domain.admin.entity.ManagementWebtoon;
import pretzel.dreamketcherbe.domain.admin.entity.ReasonContent;
import pretzel.dreamketcherbe.domain.admin.exception.AdminException;
import pretzel.dreamketcherbe.domain.admin.exception.AdminExceptionType;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRepositoryCustom;
import pretzel.dreamketcherbe.domain.admin.repository.ManagementWebtoonRespository;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.entity.WebtoonStatus;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.SerializationPeriodRepository;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

@Service
@AllArgsConstructor
public class ManageWebtoonService {

    private final WebtoonRepository webtoonRepository;

    private final ManagementWebtoonRespository managementWebtoonRespository;

    private final SerializationPeriodRepository serializationPeriodRepository;

    private final ManagementWebtoonRepositoryCustom managementWebtoonRepositoryCustom;

    /**
     * 웹툰 목록 조회
     */
    public Page<ManageWebtoonResDto> getWebtoons(Pageable pageable) {
        Page<Webtoon> webtoons = webtoonRepository.findAllByOrderByCreatedAtDesc(pageable);

        return webtoons.map(webtoon -> {
            ManagementWebtoon manangeWebtoon = managementWebtoonRespository.findByWebtoonId(
                    webtoon.getId())
                .orElseThrow(() -> new AdminException(AdminExceptionType.MANAGE_WEBTOON_NOT_FOUND));

            SerializationPeriod serializationPeriod = serializationPeriodRepository.findByWebtoonId(webtoon.getId());

            return ManageWebtoonResDto.of(webtoon, webtoon.getGenre().getName(), manangeWebtoon, serializationPeriod);
        });
    }

    /**
     * 작품 승인 여부 변경
     */
    @Transactional
    public void updateWebtoonApproval(UpdateWebtoonApprovalReqDto updateWebtoonApprovalReqDto) {
        if (!ApprovalStatus.isValidStatus(updateWebtoonApprovalReqDto.approval())) {
            throw new AdminException(AdminExceptionType.APPROVAL_NOT_FOUND);
        }

        if (!ReasonContent.isValidReason(updateWebtoonApprovalReqDto.reason())) {
            throw new AdminException((AdminExceptionType.REASON_NOT_FOUND));
        }

        for (Long id : updateWebtoonApprovalReqDto.webtoonIds()) {
            webtoonRepository.findById(id)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));
        }

        managementWebtoonRepositoryCustom.updateWebtoonApproval(updateWebtoonApprovalReqDto);
    }

    /**
     * 작품 상태 변경
     */
    @Transactional
    public void updateWebtoonStatus(UpdateWebtoonStatusReqDto updateWebtoonStatusReqDto) {
        if (!WebtoonStatus.isValidStatus(updateWebtoonStatusReqDto.status())) {
            throw new WebtoonException(WebtoonExceptionType.WEBTOON_STATUS_NOT_FOUND);
        }

        for (Long id : updateWebtoonStatusReqDto.webtoonIds()) {
            Webtoon webtoon = webtoonRepository.findById(id)
                .orElseThrow(() -> new WebtoonException(WebtoonExceptionType.WEBTOON_NOT_FOUND));
            webtoon.updateStatus(updateWebtoonStatusReqDto.status());
            webtoonRepository.save(webtoon);
        }
    }
}