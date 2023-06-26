import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListeArticleComponent } from './liste-article.component';

describe('ListeArticleComponent', () => {
  let component: ListeArticleComponent;
  let fixture: ComponentFixture<ListeArticleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListeArticleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListeArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
